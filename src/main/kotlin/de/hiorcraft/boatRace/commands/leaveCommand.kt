package de.hiorcraft.boatRace.commands

import de.hiorcraft.boatRace.race.RaceManager
import de.hiorcraft.boatRace.race.RaceState
import de.hiorcraft.boatRace.util.QueueScoreboard
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor

fun leaveCommand() = commandAPICommand("leave") {

    playerExecutor { player, _ ->
        if (RaceManager.isInQueue(player)) {
            RaceManager.leave(player)
            QueueScoreboard.removeBoard(player)
            QueueScoreboard.updateQueueBoard()
            return@playerExecutor
        }

        if (RaceManager.state != RaceState.WAITING) {
            if (RaceManager.leaveActiveRace(player)) {
                player.sendMessage("§cDu hast das Rennen verlassen.")
                return@playerExecutor
            }
        }

        player.sendMessage("§eDu bist weder in der Queue noch im Rennen.")
    }
}

