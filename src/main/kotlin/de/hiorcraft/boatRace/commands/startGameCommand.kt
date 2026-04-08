package de.hiorcraft.boatRace.commands

import de.hiorcraft.boatRace.race.RaceManager
import de.hiorcraft.boatRace.race.RaceState
import de.hiorcraft.boatRace.race.RaceTimer
import de.hiorcraft.boatRace.race.TrackManager
import de.hiorcraft.boatRace.util.BoatSpawner
import de.hiorcraft.boatRace.util.FinishLineVisualizer
import de.hiorcraft.boatRace.util.QueueScoreboard
import de.hiorcraft.boatRace.util.RaceCountdown
import de.hiorcraft.boatRace.util.RaceScoreboard
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument

fun startGameCommand() = commandTree("start") {
    withAliases("gamestat", "startgame")

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
                    player.sendMessage("§7Verfügbare Tracks: ${TrackManager.getAll().joinToString(", ") { it.id }}")
                    return@playerExecutor
                }

                if (track.startPositions.isEmpty()) {
                    player.sendMessage("§cFür diesen Track gibt es noch keine Startpositionen!")
                    player.sendMessage("§7Nutze: §e/addstartpos $trackName")
                    return@playerExecutor
                }

                val roundCount = args["rounds"] as Int
                RaceManager.totalRounds = roundCount
                RaceManager.startRace(track)

                // Teleportiere Spieler zu Startpositionen
                for ((index, racePlayer) in RaceManager.activePlayers.withIndex()) {
                    val startPosition = track.startPositions.getOrElse(index) { track.startPositions.last() }
                    racePlayer.player.teleport(startPosition)

                    // Entferne Queue-Scoreboard
                    QueueScoreboard.removeBoard(racePlayer.player)

                    // Zeige Race-Scoreboard
                    RaceScoreboard.showRaceBoard(racePlayer.player, roundCount)
                }

                player.sendMessage("§aRennen gestartet mit ${RaceManager.activePlayers.size} Spielern!")
                player.sendMessage("§7Track: §e$trackName §7| Runden: §e$roundCount")

                // Starte Countdown und dann das Rennen
                RaceCountdown.startCountdown {
                    RaceManager.state = RaceState.RUNNING

                    // Spawne Boote
                    BoatSpawner.spawnBoatsAtStartPositions()

                    // Starte Timer
                    for (racePlayer in RaceManager.activePlayers) {
                        RaceTimer.start(racePlayer.player)
                    }

                    // Zeige gemeinsame Start/Ziel-Linie dauerhaft
                    FinishLineVisualizer.startDisplayingLapLine()
                }
            }
        }
    }
}