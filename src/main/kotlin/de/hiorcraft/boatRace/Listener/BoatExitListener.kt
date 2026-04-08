package de.hiorcraft.boatRace.Listener

import de.hiorcraft.boatRace.race.RaceManager
import de.hiorcraft.boatRace.race.RaceState
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.vehicle.VehicleExitEvent

class BoatExitListener : Listener {

    @EventHandler
    fun onVehicleExit(event: VehicleExitEvent) {
        if (RaceManager.state != RaceState.RUNNING) return

        val passenger = event.exited
        val isRacePlayer = RaceManager.activePlayers.any { it.player == passenger }

        if (isRacePlayer) {
            event.isCancelled = true
            passenger.sendMessage("§cDu kannst das Boot während des Rennens nicht verlassen!")
        }
    }
}

