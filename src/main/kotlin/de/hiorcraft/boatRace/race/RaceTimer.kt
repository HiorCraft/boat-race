package de.hiorcraft.boatRace.race

import de.hiorcraft.boatRace.plugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

object RaceTimer {

    private val timers = mutableMapOf<Player, BukkitTask>()

    fun start(player: Player) {
        val startTime = System.currentTimeMillis()

        val task = Bukkit.getScheduler().runTaskTimer(
            plugin, Runnable {
                val now = System.currentTimeMillis()
                val diff = (now - startTime) / 1000.0

                player.sendActionBar("§b⏱ ${"%.1f".format(diff)}s")
            }, 0L, 2L
        )

        timers[player] = task
    }

    fun stop(player: Player): Double {
        timers[player]?.cancel()
        timers.remove(player)

        return (System.currentTimeMillis() / 1000.0)
    }
}