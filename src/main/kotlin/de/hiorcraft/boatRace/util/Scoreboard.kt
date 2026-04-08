package de.hiorcraft.boatRace.util

import de.hiorcraft.boatRace.plugin
import de.hiorcraft.boatRace.race.RaceManager
import de.hiorcraft.boatRace.race.RacePlayer
import de.hiorcraft.boatRace.race.RaceState
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scoreboard.DisplaySlot

object QueueScoreboard {

    private fun queueText(): String = "§eQueue: §b${RaceManager.queue.size} Spieler"

    fun showQueueBoard(player: Player) {
        player.sendActionBar(queueText())
    }

    fun updateQueueBoard() {
        for (player in RaceManager.queue) {
            player.sendActionBar(queueText())
        }
    }

    fun removeBoard(player: Player) {
        // ActionBar leeren, damit keine Queue-Anzeige mehr sichtbar ist.
        player.sendActionBar(" ")
    }
}

object RaceScoreboard {

    private const val LIVE_UPDATE_TICKS = 4L
    private val playerBoards = mutableMapOf<Player, org.bukkit.scoreboard.Scoreboard>()
    private var updateTask: BukkitTask? = null

    fun showRaceBoard(player: Player, totalRounds: Int) {
        val board = Bukkit.getScoreboardManager().newScoreboard
        val obj = board.registerNewObjective("race", "dummy", "§6§lBootsrennen")
        obj.displaySlot = DisplaySlot.SIDEBAR

        player.scoreboard = board
        playerBoards[player] = board

        updateBoard(player, totalRounds)
        startAutoUpdate()
    }

    fun stopAutoUpdate() {
        updateTask?.cancel()
        updateTask = null
    }

    private fun startAutoUpdate() {
        if (updateTask != null) return

        updateTask = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            if (RaceManager.state == RaceState.WAITING) {
                stopAutoUpdate()
                return@Runnable
            }

            val placements = calculatePlacements()
            for (racePlayer in RaceManager.activePlayers) {
                updateBoard(racePlayer.player, RaceManager.totalRounds, placements)
            }
        }, 0L, LIVE_UPDATE_TICKS)
    }

    private fun updateBoard(player: Player, totalRounds: Int, placements: List<RacePlayer> = calculatePlacements()) {
        val board = playerBoards[player] ?: return
        val obj = board.getObjective("race") ?: return
        val viewerRacePlayer = RaceManager.activePlayers.firstOrNull { it.player == player } ?: return

        board.entries.forEach { board.resetScores(it) }

        val lines = mutableListOf<String>()
        lines += "§e§lTop 3"

        placements.take(3).forEachIndexed { index, rp ->
            lines += formatPlacementLine(index + 1, rp, player)
        }

        lines += " "
        lines += "§b§lUm dich"

        val myRank = placements.indexOfFirst { it.player == player } + 1
        lines += if (myRank > 0) "§7Dein Platz: §e#$myRank" else "§7Dein Platz: §e-"

        if (myRank > 0) {
            val start = (myRank - 1).coerceAtLeast(1)
            val end = (myRank + 1).coerceAtMost(placements.size)

            for (rank in start..end) {
                val rp = placements[rank - 1]
                lines += formatPlacementLine(rank, rp, player)
            }
        } else {
            lines += "§7-"
        }

        lines += "  "
        lines += "§7Runde: §e${viewerRacePlayer.currentRound}/$totalRounds"
        lines += "§7Meter: §e${currentLapMeters(viewerRacePlayer)}m"

        val total = lines.size
        lines.forEachIndexed { index, line ->
            obj.getScore(uniqueEntry(line, index)).score = total - index
        }
    }

    private fun formatPlacementLine(rank: Int, racePlayer: RacePlayer, viewer: Player): String {
        val marker = if (racePlayer.player == viewer) "§aDu" else "§f${racePlayer.player.name}"
        return "§7#$rank §8- $marker"
    }

    private fun uniqueEntry(text: String, index: Int): String {
        val code = "0123456789abcdef"[index % 16]
        return "$text§$code"
    }

    private fun currentLapMeters(racePlayer: RacePlayer): Int {
        val track = RaceManager.currentTrack ?: return 0
        val lapLength = track.lapLineA.distance(track.lapLineB)
        val progress = progressOnTrack(racePlayer.player.location, track.lapLineA, track.lapLineB)
        return (lapLength * progress).toInt()
    }

    fun removeBoard(player: Player) {
        playerBoards.remove(player)
        player.scoreboard = Bukkit.getScoreboardManager().mainScoreboard
    }
}