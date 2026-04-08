package de.hiorcraft.boatRace.commands

import de.hiorcraft.boatRace.race.RaceManager
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry


fun joinCommand() = commandAPICommand("test") {

    playerExecutor { player, args ->
        RaceManager.join(player)
    }
}
