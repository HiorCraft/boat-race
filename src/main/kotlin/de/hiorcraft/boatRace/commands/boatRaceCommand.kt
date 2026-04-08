package de.hiorcraft.boatRace.commands

import de.hiorcraft.boatRace.plugin
import de.hiorcraft.boatRace.race.RaceManager
import de.hiorcraft.boatRace.race.RaceState
import de.hiorcraft.boatRace.race.TrackManager
import de.hiorcraft.boatRace.track.TrackEditor
import de.hiorcraft.boatRace.util.ChatConfig
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument

fun boatRaceCommand() = commandTree("boatrace") {
    withAliases("br")

    stringArgument("action") {
        playerExecutor { player, args ->
            val action = (args["action"] as String).lowercase()
            if (action != "reload") {
                player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.WARNING}Nutze: §e/boatrace reload")
                return@playerExecutor
            }

            if (RaceManager.state != RaceState.WAITING) {
                player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.ERROR}Reload nur möglich, wenn kein Rennen läuft.")
                return@playerExecutor
            }

            plugin.reloadConfig()
            TrackEditor.reload()
            TrackManager.load(plugin)

            player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.SUCCESS}Konfiguration neu geladen.")
            player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.INFO}Tracks geladen: §e${TrackManager.getAll().size}")
        }
    }

    playerExecutor { player, _ ->
        player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.INFO}Nutze: §e/boatrace reload")
    }
}

