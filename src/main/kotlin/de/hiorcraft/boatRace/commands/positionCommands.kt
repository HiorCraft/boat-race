package de.hiorcraft.boatRace.commands

import de.hiorcraft.boatRace.Listener.TrackEditingListener
import de.hiorcraft.boatRace.race.RaceManager
import de.hiorcraft.boatRace.track.TrackEditor
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

fun setLobbyCommand() = commandTree("setlobby") {
    playerExecutor { player, _ ->
        val loc = player.location.clone().apply { pitch = 0f }
        TrackEditor.setLobby(loc)
        RaceManager.lobbyLocation = loc
        player.sendMessage("§aLobby gesetzt!")
    }
}

fun lobbyCommand() = commandTree("lobby") {
    playerExecutor { player, _ ->
        val lobby = RaceManager.lobbyLocation
        if (lobby == null) {
            player.sendMessage("§cLobby ist nicht gesetzt. Nutze §e/setlobby")
            return@playerExecutor
        }
        player.teleport(lobby)
        player.sendMessage("§aZur Lobby teleportiert.")
    }
}

