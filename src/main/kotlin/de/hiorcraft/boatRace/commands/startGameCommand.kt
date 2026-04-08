package de.hiorcraft.boatRace.commands

import de.hiorcraft.boatRace.race.RaceManager
import de.hiorcraft.boatRace.race.RaceState
import de.hiorcraft.boatRace.race.RaceTimer
import de.hiorcraft.boatRace.race.TrackManager
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument

fun startGameCommand() = commandTree("start") {

    stringArgument("track") {
        integerArgument("rounds") {
            playerExecutor { player, args ->
                if (RaceManager.queue.isEmpty()) {
                    player.sendMessage("§cKeine Spieler in der Queue!")
                    return@playerExecutor
                }

                if (RaceManager.state != RaceState.WAITING) {
                    player.sendMessage("§cEs läuft bereits ein Rennen!")
                    return@playerExecutor
                }

                val trackName = args["track"] as String
                val track = TrackManager.get(trackName)
                if (track == null) {
                    player.sendMessage("§cTrack '$trackName' nicht gefunden!")
                    return@playerExecutor
                }

                val roundCount = args["rounds"] as Int
                RaceManager.totalRounds = roundCount
                RaceManager.startRace(track)

                for (racePlayer in RaceManager.activePlayers) {
                    RaceTimer.start(racePlayer.player)
                }

                player.sendMessage("§aRennen gestartet mit ${RaceManager.activePlayers.size} Spielern!")
                player.sendMessage("§7Track: §e$trackName §7| Runden: §e$roundCount")
            }
        }
    }
}