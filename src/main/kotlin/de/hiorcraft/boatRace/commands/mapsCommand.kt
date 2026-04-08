package de.hiorcraft.boatRace.commands

import de.hiorcraft.boatRace.race.TrackManager
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor

fun mapsCommand() = commandAPICommand("maps") {

    playerExecutor { player, _ ->

        val maps = TrackManager.getAll()

        if (maps.isEmpty()) {
            player.sendMessage("§cEs wurden noch keine Maps erstellt.")
            return@playerExecutor
        }

        player.sendMessage("§b§lVerfügbare Maps:")
        maps.forEach { track ->
            player.sendMessage("§7- §a${track.id}")
        }
    }
}