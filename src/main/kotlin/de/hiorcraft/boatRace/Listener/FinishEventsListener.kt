package de.hiorcraft.boatRace.Listener

import de.hiorcraft.boatRace.race.RaceManager
import de.hiorcraft.boatRace.race.RaceState
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class FinishEventsListener: Listener {

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (RaceManager.state != RaceState.RUNNING) return

        val track = RaceManager.currentTrack ?: return
        val racePlayer = RaceManager.activePlayers.firstOrNull { it.player == event.player } ?: return

        if (racePlayer.currentRound > RaceManager.totalRounds) {
            racePlayer.finished = true
            RaceManager.finishPlayer(racePlayer)
        }
    }
}