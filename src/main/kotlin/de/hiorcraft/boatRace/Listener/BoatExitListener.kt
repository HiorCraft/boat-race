package de.hiorcraft.boatRace.Listener

import de.hiorcraft.boatRace.plugin
import de.hiorcraft.boatRace.race.RaceManager
import de.hiorcraft.boatRace.race.RaceState
import de.hiorcraft.boatRace.util.BoatSpawner
import de.hiorcraft.boatRace.util.ChatConfig
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.vehicle.VehicleExitEvent
import java.util.UUID

class BoatExitListener : Listener {

    private val lastRespawnMillis = mutableMapOf<UUID, Long>()

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onVehicleExit(event: VehicleExitEvent) {
        if (RaceManager.state != RaceState.RUNNING) return

        val player = event.exited as? Player ?: return
        val racePlayer = RaceManager.activePlayers.firstOrNull { it.player.uniqueId == player.uniqueId } ?: return
        if (racePlayer.finished) return

        plugin.server.scheduler.runTaskLater(plugin, Runnable {
            if (RaceManager.state != RaceState.RUNNING) return@Runnable
            if (racePlayer.finished || player.isInsideVehicle) return@Runnable

            val now = System.currentTimeMillis()
            val last = lastRespawnMillis[player.uniqueId] ?: 0L
            if (now - last < 1200L) return@Runnable
            lastRespawnMillis[player.uniqueId] = now

            BoatSpawner.respawnBoatFor(racePlayer, player.location)
            player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.WARNING}Du bist aus dem Boot gefallen - Respawn!")
        }, 1L)
    }
}
