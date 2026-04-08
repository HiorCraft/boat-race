package de.hiorcraft.boatRace.commands


import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor


fun joinCommand() = commandAPICommand("test") {

    playerExecutor { player, args ->

        if (RaceManager.isInQueue(player)) {
            player.sendMessage("§eDu bist bereits in der Queue.")
            return@executesPlayer
        }

        if (RaceManager.state != RaceState.WAITING) {
            player.sendMessage("§cDas Rennen läuft bereits!")
            return@executesPlayer
        }

        RaceManager.join(player)
    }
}


