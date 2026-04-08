package de.hiorcraft.boatRace

import de.hiorcraft.boatRace.Listener.FinishEventsListener
import de.hiorcraft.boatRace.Listener.PlayerMoveListener
import de.hiorcraft.boatRace.Listener.TrackEditingListener
import de.hiorcraft.boatRace.commands.addStartPosCommand
import de.hiorcraft.boatRace.commands.createMapCommand
import de.hiorcraft.boatRace.commands.joinCommand
import de.hiorcraft.boatRace.commands.mapsCommand
import de.hiorcraft.boatRace.commands.setFinishLineCommand
import de.hiorcraft.boatRace.commands.setSpectatorCommand
import de.hiorcraft.boatRace.commands.setStartLineCommand
import de.hiorcraft.boatRace.commands.startGameCommand
import de.hiorcraft.boatRace.race.TrackManager

import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : JavaPlugin() {

    val manager = server.pluginManager

    private val LOGO = """
  ____              _     ____
 | __ )  ___   ___ | |_  | __ ) _ __ ___   ___
 |  _ \ / _ \ / _ \| __| |  _ \| '__/ _ \ / _ \
 | |_) | (_) | (_) | |_  | |_) | | | (_) |  __/
 |____/ \___/ \___/ \__| |____/|_|  \___/ \___|
                   BOAT - RACE
""".trimIndent()



    override fun onEnable() {

        logger.info("\n$LOGO")
        logger.info("Boat-Race is Starting!")

        // Load tracks
        TrackManager.load(this)

        // Register commands
        startGameCommand()
        joinCommand()
        setStartLineCommand()
        setFinishLineCommand()
        createMapCommand()
        mapsCommand()
        addStartPosCommand()
        setSpectatorCommand()

        // Register listeners
        manager.registerEvents(FinishEventsListener(), this)
        manager.registerEvents(PlayerMoveListener(), this)
        manager.registerEvents(TrackEditingListener, this)

        logger.info("Commands registered!")
        logger.info("Listeners registered!")


    }

    override fun onDisable() {
    }
}
