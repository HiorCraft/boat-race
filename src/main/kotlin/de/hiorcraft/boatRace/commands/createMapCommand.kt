package de.hiorcraft.boatRace.commands

import de.hiorcraft.boatRace.track.TrackEditor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor

fun createMapCommand() = commandTree("createMap") {

         playerExecutor { player, args ->
             val name = args[0] as String

             if (!TrackEditor.createMap(name)) {
                 player.sendMessage("§cMap existiert bereits!")
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