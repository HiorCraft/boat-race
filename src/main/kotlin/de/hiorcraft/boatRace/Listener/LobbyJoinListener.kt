package de.hiorcraft.boatRace.Listener

import de.hiorcraft.boatRace.plugin
import de.hiorcraft.boatRace.race.RaceManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class LobbyJoinListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val lobby = RaceManager.lobbyLocation ?: return

        // 1 Tick verzögert teleportieren, damit der Spawn des Servers nicht dazwischenfunkt.
        plugin.server.scheduler.runTaskLater(plugin, Runnable {
            event.player.teleport(lobby)
        }, 1L)
    }
}

