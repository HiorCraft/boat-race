package de.hiorcraft.boatRace.Listener

import de.hiorcraft.boatRace.track.TrackEditor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

object TrackEditingListener : Listener {

    private val editingStartLine = mutableMapOf<Player, String>()
    private val firstPointStart = mutableMapOf<Player, org.bukkit.Location>()

    private val editingFinishLine = mutableMapOf<Player, String>()
    private val firstPointFinish = mutableMapOf<Player, org.bukkit.Location>()

    private val editingStartPos = mutableMapOf<Player, String>()
    private val editingSpectator = mutableMapOf<Player, String>()

    fun startEditingStartLine(player: Player, map: String) {
        editingStartLine[player] = map
        player.sendMessage("§aKlicke den ersten Punkt der Startlinie.")
    }

    fun startEditingFinishLine(player: Player, map: String) {
        editingFinishLine[player] = map
        player.sendMessage("§aKlicke den ersten Punkt der Ziellinie.")
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

        // Startline editing
        val startMap = editingStartLine[player]
        if (startMap != null) {
            if (e.action != Action.RIGHT_CLICK_BLOCK) return
            val block = e.clickedBlock ?: return
            val loc = block.location

            if (!firstPointStart.containsKey(player)) {
                firstPointStart[player] = loc
                player.sendMessage("§aErster Punkt gesetzt! Jetzt zweiten klicken.")
            } else {
                TrackEditor.setStartLine(startMap, firstPointStart[player]!!, loc)
                player.sendMessage("§aStartlinie gespeichert!")
                editingStartLine.remove(player)
                firstPointStart.remove(player)
            }
            e.isCancelled = true
            return
        }

        // Finishline editing
        val finishMap = editingFinishLine[player]
        if (finishMap != null) {
            if (e.action != Action.RIGHT_CLICK_BLOCK) return
            val block = e.clickedBlock ?: return
            val loc = block.location

            if (!firstPointFinish.containsKey(player)) {
                firstPointFinish[player] = loc
                player.sendMessage("§aErster Punkt gesetzt! Jetzt zweiten klicken.")
            } else {
                TrackEditor.setFinishLine(finishMap, firstPointFinish[player]!!, loc)
                player.sendMessage("§aZiellinie gespeichert!")
                editingFinishLine.remove(player)
                firstPointFinish.remove(player)
            }
            e.isCancelled = true
            return
        }

        // Start position editing
        val startPosMap = editingStartPos[player]
        if (startPosMap != null) {
            if (e.action != Action.RIGHT_CLICK_BLOCK) return
            val block = e.clickedBlock ?: return
            val loc = block.location

            TrackEditor.setStartPosition(startPosMap, loc)
            player.sendMessage("§aStartposition gespeichert!")
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

        }
    }
}
