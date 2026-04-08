package de.hiorcraft.boatRace.Listener

import de.hiorcraft.boatRace.track.TrackEditor
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
        player.sendMessage("§aKlicke Punkt A der Start/Ziel-Linie.")
    }

    fun startEditingStartPos(player: Player, map: String) {
        editingStartPos[player] = map
        player.sendMessage("§aKlicke die Startposition.")
    }

    fun startEditingSpectator(player: Player, map: String) {
        editingSpectator[player] = map
        player.sendMessage("§aKlicke die Zuschauerposition.")
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
                player.sendMessage("§aPunkt A gesetzt! Jetzt Punkt B klicken.")
            } else {
                val pointA = firstPointLap[player]!!
                TrackEditor.setLapLine(lapMap, pointA, loc)
                val direction = DirectionHelper.getDirection(pointA, loc)
                val directionText = DirectionHelper.getDirectionSymbol(direction)
                player.sendMessage("§aStart/Ziel-Linie gespeichert! $directionText")
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
            player.sendMessage("§aStartposition mit Richtung hinzugefügt!")
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
            player.sendMessage("§aZuschauerposition gespeichert!")
            editingSpectator.remove(player)
            e.isCancelled = true
            return
        }
    }
}
