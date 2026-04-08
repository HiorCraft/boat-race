package de.hiorcraft.boatRace.Listener

import de.hiorcraft.boatRace.track.TrackEditor
import de.hiorcraft.boatRace.util.ChatConfig
import de.hiorcraft.boatRace.util.DirectionHelper
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

object TrackEditingListener : Listener {

    private val editingLapLine = mutableMapOf<Player, String>()
    private val firstPointLap = mutableMapOf<Player, org.bukkit.Location>()

    private val editingStartPos = mutableMapOf<Player, String>()
    private val editingSpectator = mutableMapOf<Player, String>()

    fun startEditingLapLine(player: Player, map: String) {
        editingLapLine[player] = map
        player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.SUCCESS}Linien-Editor aktiv fuer §e$map${ChatConfig.SUCCESS}.")
        player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.INFO}Schritt 1: Rechtsklick auf Punkt A.")
    }

    fun startEditingStartPos(player: Player, map: String) {
        editingStartPos[player] = map
        player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.SUCCESS}Startpositions-Editor aktiv fuer §e$map${ChatConfig.SUCCESS}.")
        player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.INFO}Schritt 1: In Fahrtrichtung schauen.")
        player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.INFO}Schritt 2: Rechtsklick auf den Startblock.")
    }

    fun startEditingSpectator(player: Player, map: String) {
        editingSpectator[player] = map
        player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.SUCCESS}Zuschauerpunkt-Editor aktiv fuer §e$map${ChatConfig.SUCCESS}.")
        player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.INFO}Rechtsklick auf die Zuschauerposition.")
    }

    @EventHandler
    fun onClick(e: PlayerInteractEvent) {
        val player = e.player

        // Lap line editing
        val lapMap = editingLapLine[player]
        if (lapMap != null) {
            if (e.action != Action.RIGHT_CLICK_BLOCK) return
            val block = e.clickedBlock ?: return
            val loc = block.location

            if (!firstPointLap.containsKey(player)) {
                firstPointLap[player] = loc
                player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.SUCCESS}Punkt A gespeichert.")
                player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.INFO}Jetzt Punkt B mit Rechtsklick setzen.")
            } else {
                val pointA = firstPointLap[player]!!
                TrackEditor.setLapLine(lapMap, pointA, loc)
                val direction = DirectionHelper.getDirection(pointA, loc)
                val directionText = DirectionHelper.getDirectionSymbol(direction)
                player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.SUCCESS}Start/Ziel-Linie gespeichert! $directionText")
                player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.INFO}Map: §e$lapMap")
                editingLapLine.remove(player)
                firstPointLap.remove(player)
            }
            e.isCancelled = true
            return
        }

        // Start position editing
        val startPosMap = editingStartPos[player]
        if (startPosMap != null) {
            if (e.action != Action.RIGHT_CLICK_BLOCK) return
            val block = e.clickedBlock ?: return
            val loc = block.location.add(0.5, 0.0, 0.5).apply {
                yaw = player.location.yaw
                pitch = 0f
            }

            TrackEditor.addStartPosition(startPosMap, loc)
            player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.SUCCESS}Startposition gespeichert.")
            player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.INFO}Map: §e$startPosMap §7| Yaw: §e${"%.1f".format(loc.yaw)}")
            player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.INFO}Tipp: §e/previewboats $startPosMap §7zum Pruefen der Aufstellung.")
            editingStartPos.remove(player)
            e.isCancelled = true
            return
        }

        // Spectator position editing
        val spectatorMap = editingSpectator[player]
        if (spectatorMap != null) {
            if (e.action != Action.RIGHT_CLICK_BLOCK) return
            val block = e.clickedBlock ?: return
            val loc = block.location

            TrackEditor.setSpectator(spectatorMap, loc)
            player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.SUCCESS}Zuschauerposition gespeichert.")
            player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.INFO}Map: §e$spectatorMap")
            editingSpectator.remove(player)
            e.isCancelled = true
            return
        }
    }
}
