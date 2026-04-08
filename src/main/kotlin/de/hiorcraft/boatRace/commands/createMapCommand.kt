package de.hiorcraft.boatRace.commands

import de.hiorcraft.boatRace.track.TrackEditor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument

fun createMapCommand() = commandTree("createMap") {

    stringArgument("name") {

        playerExecutor { player, args ->
            val name = args["name"] as String

            if (!TrackEditor.createMap(name)) {
                player.sendMessage("§cMap existiert bereits!")
                return@playerExecutor
            }

            player.sendMessage("§aMap §e$name §aerstellt!")
            player.sendMessage("§7Setze jetzt:")
            player.sendMessage("§b/setstartline $name")
            player.sendMessage("§b/setfinishline $name")
            player.sendMessage("§b/addstartpos $name")
            player.sendMessage("§b/setspectator $name")
        }
    }
}
