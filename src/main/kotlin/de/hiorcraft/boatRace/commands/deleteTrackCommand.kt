package de.hiorcraft.boatRace.commands

import de.hiorcraft.boatRace.track.TrackEditor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument

fun deleteTrackCommand() = commandTree("deletetrack") {

    stringArgument("map") {
        playerExecutor { player, args ->
            val map = args["map"] as String

            if (!TrackEditor.deleteMap(map)) {
                player.sendMessage("§cMap nicht gefunden: §e$map")
                return@playerExecutor
            }

            player.sendMessage("§aMap §e$map §agelöscht.")
        }
    }
}

