package de.hiorcraft.boatRace.util


import de.hiorcraft.boatRace.plugin
import org.bukkit.Bukkit


fun updateAllScoreboards() {
    val placements = calculatePlacements()

    placements.forEachIndexed { index, rp ->
        ScoreboardManager.update(
            rp.player,
            rp.currentRound,
            totalRounds,
            place = index + 1,
            time = rp.totalTime
        )
    }

    fun startLiveUpdates() {
        Bukkit.getScheduler().runTaskTimer(
            plugin,
            Runnable { updateAllScoreboards() },
            0L, 5L // alle 0.25 Sekunden
        )
    }

}
