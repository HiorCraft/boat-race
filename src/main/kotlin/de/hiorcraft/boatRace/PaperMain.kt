package de.hiorcraft.boatRace

import de.hiorcraft.boatRace.Listener.BoatExitListener
import de.hiorcraft.boatRace.Listener.FinishEventsListener
import de.hiorcraft.boatRace.Listener.LobbyJoinListener
import de.hiorcraft.boatRace.Listener.PlayerMoveListener
import de.hiorcraft.boatRace.Listener.TrackEditingListener
import de.hiorcraft.boatRace.commands.addStartPosCommand
import de.hiorcraft.boatRace.commands.createMapCommand
import de.hiorcraft.boatRace.commands.deleteTrackCommand
import de.hiorcraft.boatRace.commands.joinCommand
import de.hiorcraft.boatRace.commands.leaveCommand
import de.hiorcraft.boatRace.commands.mapsCommand
import de.hiorcraft.boatRace.commands.previewBoatsCommand
import de.hiorcraft.boatRace.commands.lobbyCommand
import de.hiorcraft.boatRace.commands.setLapLineCommand
import de.hiorcraft.boatRace.commands.setLobbyCommand
import de.hiorcraft.boatRace.commands.setPodestCommand
import de.hiorcraft.boatRace.commands.setSpectatorCommand
import de.hiorcraft.boatRace.commands.stopGameCommand
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

        saveDefaultConfig()
        TrackManager.load(this)

        // Register commands
        setLapLineCommand()
        previewBoatsCommand()
        createMapCommand()
        deleteTrackCommand()
        mapsCommand()
        addStartPosCommand()
        setLobbyCommand()
        lobbyCommand()
        setPodestCommand()
        startGameCommand()
        stopGameCommand()
        joinCommand()
        leaveCommand()
        setSpectatorCommand()


        // Register listeners
        manager.registerEvents(FinishEventsListener(), this)
        manager.registerEvents(BoatExitListener(), this)
        manager.registerEvents(LobbyJoinListener(), this)
        manager.registerEvents(PlayerMoveListener(), this)
        manager.registerEvents(TrackEditingListener, this)

        logger.info("Commands registered!")
        logger.info("Listeners registered!")

    }

    override fun onDisable() {
    }
}
