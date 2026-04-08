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

    fun update(player: Player, round: Int, totalRounds: Int, place: Int, time: Double) {
        val board = boards[player] ?: return
        val obj = board.getObjective("race") ?: return

        board.entries.forEach { board.resetScores(it) }

        obj.getScore("§7Runde: $round/$totalRounds").score = 3
        obj.getScore("§7Platz: ${if (place == 0) "?" else place}").score = 2
        obj.getScore("§7Zeit: ${"%.1f".format(time)}s").score = 1
    }


}