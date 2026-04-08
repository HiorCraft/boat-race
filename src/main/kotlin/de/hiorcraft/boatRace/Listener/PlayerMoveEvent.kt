package de.hiorcraft.boatRace.Listener

import de.hiorcraft.boatRace.race.RaceManager
import de.hiorcraft.boatRace.race.RaceTimer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener


class PlayerMoveEvent: Listener {

    @EventHandler
    fun onMove(e: PlayerMoveEvent) {
        val rp = RaceManager.getRacePlayer(e.player) ?: return

        // Ziellinie checken
        if (crossedFinish(e.player)) {
            rp.currentRound++

            if (rp.currentRound > RaceManager.totalRounds) {
                rp.finished = true
                rp.totalTime = RaceTimer.stop(rp.player)
            }
        }
    }
}