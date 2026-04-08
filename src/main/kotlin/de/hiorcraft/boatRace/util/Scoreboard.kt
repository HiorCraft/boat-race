package de.hiorcraft.boatRace.util

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot

object Scoreboard {

    private val board = mutableMapOf<Player, Scoreboard>()

    fun create(player: Player, totalRounds: Int) {
        val board = Bukkit.getScoreboardManager().newScoreboard
        val obj = board.registerNewObjective("race", "dummy", "§6§lBootsrennen")
        obj.displaySlot = DisplaySlot.SIDEBAR

        obj.getScore("§7Runden: §e0/$totalRounds").score = 3
        obj.getScore("§7Platz: ?").score = 2
        obj.getScore("§7Zeit: 0.0s").score = 1
    }
}