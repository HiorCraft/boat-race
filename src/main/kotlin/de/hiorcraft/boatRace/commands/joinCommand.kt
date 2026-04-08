package de.hiorcraft.boatRace.commands

import de.hiorcraft.boatRace.race.RaceManager
import de.hiorcraft.boatRace.race.RaceState
import de.hiorcraft.boatRace.util.QueueScoreboard
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor


fun joinCommand() = commandAPICommand("join") {

    playerExecutor { player, args ->

        if (RaceManager.state != RaceState.WAITING) {
            player.sendMessage("§cDas Rennen läuft bereits!")
            return@playerExecutor
        }

        // Schon drin?
        if (RaceManager.isInQueue(player)) {
            player.sendMessage("§eDu bist bereits in der Queue.")
            return@playerExecutor
        }

        // Joinen
        RaceManager.join(player)

        // Scoreboard anzeigen
        QueueScoreboard.showQueueBoard(player)
        QueueScoreboard.updateQueueBoard()
    }
}
