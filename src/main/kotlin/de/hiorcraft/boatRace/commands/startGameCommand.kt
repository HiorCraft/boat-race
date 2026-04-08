package de.hiorcraft.boatRace.commands

import de.hiorcraft.boatRace.plugin
import de.hiorcraft.boatRace.race.RaceManager
import de.hiorcraft.boatRace.race.RaceState
import de.hiorcraft.boatRace.race.RaceTimer
import de.hiorcraft.boatRace.race.TrackManager
import de.hiorcraft.boatRace.util.BoatSpawner
import de.hiorcraft.boatRace.util.ChatConfig
import de.hiorcraft.boatRace.util.FinishLineVisualizer
import de.hiorcraft.boatRace.util.QueueScoreboard
import de.hiorcraft.boatRace.util.RaceCountdown
import de.hiorcraft.boatRace.util.RaceScoreboard
import de.hiorcraft.boatRace.util.getGridStartPosition
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument
import org.bukkit.GameMode

fun startGameCommand() = commandTree("start") {

    stringArgument("track") {
        integerArgument("rounds") {
            playerExecutor { player, args ->
                if (RaceManager.queue.isEmpty()) {
                    player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.ERROR}Keine Spieler in der Queue!")
                    return@playerExecutor
                }

                if (RaceManager.state != RaceState.WAITING) {
                    player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.ERROR}Es läuft bereits ein Rennen!")
                    return@playerExecutor
                }

                val trackName = args["track"] as String
                val track = TrackManager.get(trackName)
                if (track == null) {
                    player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.ERROR}Track '$trackName' nicht gefunden!")
                    player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.INFO}Verfügbare Tracks: ${TrackManager.getAll().joinToString(", ") { it.id }}")
                    return@playerExecutor
                }

                if (track.startPositions.isEmpty()) {
                    player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.ERROR}Für diesen Track gibt es noch keine Startpositionen!")
                    player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.INFO}Nutze: §e/addstartpos $trackName")
                    return@playerExecutor
                }

                val roundCount = args["rounds"] as Int
                RaceManager.totalRounds = roundCount
                RaceManager.startRace(track)

                // Bereite Spieler vor, Teleport zur Strecke folgt nach Lobby-Countdown
                for (racePlayer in RaceManager.activePlayers) {
                    racePlayer.player.gameMode = GameMode.SURVIVAL

                    // Entferne Queue-Scoreboard
                    QueueScoreboard.removeBoard(racePlayer.player)

                    // Zeige Race-Scoreboard
                    RaceScoreboard.showRaceBoard(racePlayer.player, roundCount)
                }

                val lobbySeconds = plugin.config.getInt("lobbyCountdown", 10).coerceAtLeast(1)
                val mapSeconds = plugin.config.getInt("mapCountdown", 10).coerceAtLeast(1)
                val startSeconds = plugin.config.getInt("startCountdown", 3).coerceAtLeast(1)

                player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.SUCCESS}Rennen vorbereitet mit ${RaceManager.activePlayers.size} Spielern!")
                player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.INFO}Track: §e$trackName §7| Runden: §e$roundCount")
                player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.INFO}Ablauf: §e${lobbySeconds}s Lobby §7-> §eTeleport §7-> §e${mapSeconds}s Vorbereitung §7-> §e${startSeconds}s Start")

                // Starte konfigurierbare Countdown-Phasen
                RaceCountdown.startLobbyAndMapCountdown(
                    lobbySeconds = lobbySeconds,
                    mapSeconds = mapSeconds,
                    startSeconds = startSeconds,
                    onMapTeleport = {
                        for ((index, racePlayer) in RaceManager.activePlayers.withIndex()) {
                            val startPosition = track.getGridStartPosition(index) ?: continue
                            racePlayer.player.teleport(startPosition)
                        }
                    }
                ) {
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