package de.hiorcraft.boatRace.Listener

import de.hiorcraft.boatRace.race.RaceManager
import de.hiorcraft.boatRace.race.RaceState
import de.hiorcraft.boatRace.race.RaceTimer
import de.hiorcraft.boatRace.util.progressOnTrack
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent


class PlayerMoveListener: Listener {

    @EventHandler
    fun onMove(e: PlayerMoveEvent) {
        if (RaceManager.state != RaceState.RUNNING) return

        val track = RaceManager.currentTrack ?: return
        val rp = RaceManager.activePlayers.firstOrNull { it.player == e.player } ?: return

        if (crossedFinish(e.player, track)) {
            rp.currentRound++

            if (rp.currentRound > RaceManager.totalRounds) {
                rp.finished = true
                rp.totalTime = RaceTimer.stop(rp.player)
                RaceManager.finishPlayer(rp)
            }
        }
    }

    private fun crossedFinish(player: org.bukkit.entity.Player, track: de.hiorcraft.boatRace.race.RaceTrack): Boolean {
        val progress = progressOnTrack(player.location, track.finishLineA, track.finishLineB)
        return progress >= 0.5
    }
}