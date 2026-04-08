package de.hiorcraft.boatRace

import de.hiorcraft.boatRace.commands.createMapCommand
import de.hiorcraft.boatRace.commands.joinCommand
import de.hiorcraft.boatRace.commands.setStartLineCommand
import de.hiorcraft.boatRace.commands.startGameCommand

import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : JavaPlugin() {

    val manager = server.pluginManager

    override fun onEnable() {
        logger.info("Boat-Race is Starting!")

        startGameCommand()
        joinCommand()
        setStartLineCommand()
        createMapCommand()

        logger.info("Commands registered!")


    }

    override fun onDisable() {
    }
}
