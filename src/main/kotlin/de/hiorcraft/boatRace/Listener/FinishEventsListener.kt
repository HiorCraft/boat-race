package de.hiorcraft.boatRace.Listener

import de.hiorcraft.boatRace.race.RaceManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class FinishEventsListener: Listener {

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val rp = RaceManager.getRacePlayer(e.player) ?: return
        val track = RaceManager.currentTrack ?: return

        if (rp.currentRound > RaceManager.totalRounds) {
            rp.finished = true
            RaceManager.finishPlayer(rp)
        }
    }
}