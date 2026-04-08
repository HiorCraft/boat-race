package de.hiorcraft.boatRace.commands

import de.hiorcraft.boatRace.Listener.TrackEditingListener
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument

fun addStartPosCommand() = commandTree("addstartpos") {

    stringArgument("map") {
        playerExecutor { player, args ->
            val map = args["map"] as String
            TrackEditingListener.startEditingStartPos(player, map)
        }
    }
}

fun setSpectatorCommand() = commandTree("setspectator") {

    stringArgument("map") {
        playerExecutor { player, args ->
            val map = args["map"] as String
            TrackEditingListener.startEditingSpectator(player, map)
        }
    }
}
