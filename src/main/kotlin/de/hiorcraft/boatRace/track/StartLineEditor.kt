package de.hiorcraft.boatRace.track

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

object StartLineEditor {

    private val editing = mutableMapOf<Player, String>()
    private val firstPoint = mutableMapOf<Player, Location>()

    fun start(player: Player, map: String) {
        editing[player] = map
        player.sendMessage("§aKlicke den ersten Punkt der Startlinie.")
    }

    @EventHandler
    fun onClick(e: PlayerInteractEvent) {
        val player = e.player
        val map = editing[player] ?: return
        if (e.action != Action.RIGHT_CLICK_BLOCK) return

        val loc = e.clickedBlock!!.location

        if (!firstPoint.containsKey(player)) {
            firstPoint[player] = loc
            player.sendMessage("§aErster Punkt gesetzt! Jetzt zweiten klicken.")
        } else {
            val a = firstPoint[player]!!
            val b = loc

            TrackEditor.setStartLine(map, a, b)
            player.sendMessage("§aStartlinie gespeichert!")

            editing.remove(player)
            firstPoint.remove(player)
        }
    }
}