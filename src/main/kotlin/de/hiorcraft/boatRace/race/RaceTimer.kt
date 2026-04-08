package de.hiorcraft.boatRace.race

import de.hiorcraft.boatRace.plugin
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import kotlin.math.hypot

object RaceTimer {

    private val timers = mutableMapOf<Player, BukkitTask>()
    private val startTimes = mutableMapOf<Player, Long>()
    private val lastSampleMillis = mutableMapOf<Player, Long>()
    private val lastSampleLoc = mutableMapOf<Player, Location>()
    private val smoothedSpeed = mutableMapOf<Player, Double>()

    fun start(player: Player) {
        val startTime = System.currentTimeMillis()
        startTimes[player] = startTime

        val initialLoc = getMeasurementLocation(player)
        lastSampleLoc[player] = initialLoc
        lastSampleMillis[player] = startTime
        smoothedSpeed[player] = 0.0

        val task = Bukkit.getScheduler().runTaskTimer(
            plugin, Runnable {
                val now = System.currentTimeMillis()
                val diff = (now - startTime) / 1000.0
                val previousLoc = lastSampleLoc[player] ?: getMeasurementLocation(player)
                val previousMillis = lastSampleMillis[player] ?: now
                val currentLoc = getMeasurementLocation(player)
                val dt = ((now - previousMillis).coerceAtLeast(1L)) / 1000.0

                val dx = currentLoc.x - previousLoc.x
                val dz = currentLoc.z - previousLoc.z
                val horizontalDistance = hypot(dx, dz)

                val instantSpeed = if (horizontalDistance > 30.0) 0.0 else horizontalDistance / dt
                val oldSpeed = smoothedSpeed[player] ?: instantSpeed
                val speedBlocksPerSecond = oldSpeed * 0.65 + instantSpeed * 0.35

                smoothedSpeed[player] = speedBlocksPerSecond
                lastSampleLoc[player] = currentLoc
                lastSampleMillis[player] = now

                val text = "§bZeit: ${"%.1f".format(diff)}s §8| §aSpeed: ${"%.2f".format(speedBlocksPerSecond)} b/s"
                player.sendActionBar(Component.text(text))
            }, 0L, 2L
        )

        timers[player] = task
    }

    fun stop(player: Player): Double {
        timers[player]?.cancel()
        timers.remove(player)
        lastSampleMillis.remove(player)
        lastSampleLoc.remove(player)
        smoothedSpeed.remove(player)

        val start = startTimes.remove(player) ?: return 0.0
        return (System.currentTimeMillis() - start) / 1000.0
    }

    private fun getMeasurementLocation(player: Player): Location {
        return (player.vehicle?.location ?: player.location).clone()
    }
}