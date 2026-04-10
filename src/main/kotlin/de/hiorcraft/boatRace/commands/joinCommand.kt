package de.hiorcraft.boatRace.commands

import de.hiorcraft.boatRace.race.RaceManager
import de.hiorcraft.boatRace.util.QueueScoreboard
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor


fun joinCommand() = commandAPICommand("join") {

    playerExecutor { player, args ->
        // Join-Checks (State, Doppel-Join, Limit) zentral in RaceManager
        if (!RaceManager.join(player)) return@playerExecutor

        // Scoreboard anzeigen
        QueueScoreboard.showQueueBoard(player)
        QueueScoreboard.updateQueueBoard()
    }
}
