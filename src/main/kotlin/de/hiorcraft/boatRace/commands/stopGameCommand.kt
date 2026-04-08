package de.hiorcraft.boatRace.commands

import de.hiorcraft.boatRace.race.RaceManager
import de.hiorcraft.boatRace.race.RaceState
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor

fun stopGameCommand() = commandAPICommand("stopgame") {
    playerExecutor { player, _ ->
        if (RaceManager.state == RaceState.WAITING) {
            player.sendMessage("§eEs läuft aktuell kein Rennen.")
            return@playerExecutor
        }

        RaceManager.endRace(showPodium = false)
        player.sendMessage("§cRennen wurde gestoppt.")
    }
}

