package de.hiorcraft.boatRace.commands

import de.hiorcraft.boatRace.track.TrackValidationService
import de.hiorcraft.boatRace.util.ChatConfig
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument

fun validateTrackCommand() = commandTree("validatetrack") {
    stringArgument("map") {
        playerExecutor { player, args ->
            val mapId = args["map"] as String
            val result = TrackValidationService.validate(mapId)

            if (result.errors.isEmpty() && result.warnings.isEmpty()) {
                player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.SUCCESS}Track '$mapId' ist valide.")
                return@playerExecutor
            }

            player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.INFO}Validierung für §e$mapId")
            result.errors.forEach {
                player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.ERROR}$it")
            }
            result.warnings.forEach {
                player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.WARNING}$it")
            }
        }
    }
}

