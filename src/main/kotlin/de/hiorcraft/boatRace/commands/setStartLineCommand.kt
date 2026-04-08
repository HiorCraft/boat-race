package de.hiorcraft.boatRace.commands

import de.hiorcraft.boatRace.track.TrackEditor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent

private val editing = mutableMapOf<Player, String>()
private val firstPoint = mutableMapOf<Player, org.bukkit.Location>()

fun setStartLineCommand() = commandTree("setstartline") {

    playerExecutor { player, args ->
        val map = args[0] as String
        editing[player] = map
        player.sendMessage("§aKlicke den ersten Punkt der Startlinie.")
    }
}

@EventHandler
fun onClick(e: PlayerInteractEvent) {
    val player = e.player
    val map = editing[player] ?: return
    val block = e.clickedBlock ?: return

    val loc = block.location

    if (!firstPoint.containsKey(player)) {
        firstPoint[player] = loc
        player.sendMessage("§aErster Punkt gesetzt! Jetzt zweiten klicken.")
    } else {
        TrackEditor.setStartLine(map, firstPoint[player]!!, loc)
        player.sendMessage("§aStartlinie gespeichert!")
        editing.remove(player)
        firstPoint.remove(player)
    }
}
