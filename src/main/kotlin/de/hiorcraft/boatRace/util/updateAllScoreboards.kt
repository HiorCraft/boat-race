package de.hiorcraft.boatRace.util

import de.hiorcraft.boatRace.plugin
import de.hiorcraft.boatRace.race.RaceManager
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Boat
import org.bukkit.entity.EntityType
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scheduler.BukkitRunnable

object RaceCountdown {

    fun startCountdown(onStart: () -> Unit) {
        var count = 3

        for (player in RaceManager.activePlayers) {
            player.player.sendTitle("§e$count", "", 0, 20, 0)
        }

        object : BukkitRunnable() {
            override fun run() {
                count--

                if (count > 0) {
                    for (player in RaceManager.activePlayers) {
                        player.player.sendTitle("§e$count", "", 0, 20, 0)
                        player.player.playSound(player.player.location, org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)
                    }
                } else if (count == 0) {
                    for (player in RaceManager.activePlayers) {
                        player.player.sendTitle("§a§lGO!", "", 0, 20, 10)
                        player.player.playSound(player.player.location, org.bukkit.Sound.BLOCK_DISPENSER_DISPENSE, 1f, 1f)
                    }
                    cancel()
                    onStart()
                }
            }
        }.runTaskTimer(plugin, 0L, 20L)
    }
}

object BoatSpawner {

    private val spawnedRaceBoats = mutableListOf<Boat>()

    fun clearSpawnedBoats() {
        spawnedRaceBoats.forEach { if (it.isValid) it.remove() }
        spawnedRaceBoats.clear()
    }

    fun spawnBoatsAtStartPositions() {
        val track = RaceManager.currentTrack ?: return
        if (track.startPositions.isEmpty()) return
        clearSpawnedBoats()

        for ((index, racePlayer) in RaceManager.activePlayers.withIndex()) {
            val baseLoc = track.startPositions.getOrElse(index) { track.startPositions.last() }
            val world = baseLoc.world ?: continue

            if (!world.isChunkLoaded(baseLoc.blockX shr 4, baseLoc.blockZ shr 4)) {
                world.loadChunk(baseLoc.blockX shr 4, baseLoc.blockZ shr 4)
            }

            val spawnLoc = if (index == 0) {
                baseLoc.block.location.add(0.5, 0.0, 0.5)
            } else {
                baseLoc.clone().add(0.5, 0.2, 0.5)
            }
            spawnLoc.yaw = baseLoc.yaw
            spawnLoc.pitch = 0f
            val player = racePlayer.player

            if (player.isInsideVehicle) {
                player.leaveVehicle()
            }

            player.teleport(spawnLoc)

            val boat = world.spawnEntity(spawnLoc, EntityType.OAK_BOAT) as Boat
            spawnedRaceBoats.add(boat)
            if (!boat.addPassenger(player)) {
                player.teleport(boat.location)
                boat.addPassenger(player)
            }
        }
    }
}

object FinishLineVisualizer {

    private var lineTask: BukkitTask? = null

    fun drawLapLine() {
        val track = RaceManager.currentTrack ?: return

        drawParticleLine(track.lapLineA, track.lapLineB, Particle.FLAME)
    }

    fun drawStartLine() {
        drawLapLine()
    }

    fun drawFinishLine() {
        drawLapLine()
    }

    fun startDisplayingLapLine() {
        lineTask?.cancel()
        lineTask = object : BukkitRunnable() {
            override fun run() {
                if (RaceManager.state != de.hiorcraft.boatRace.race.RaceState.RUNNING) {
                    cancel()
                    lineTask = null
                    return
                }

                drawLapLine()
            }
        }.runTaskTimer(plugin, 0L, 10L)
    }

    fun stopDisplayingLapLine() {
        lineTask?.cancel()
        lineTask = null
    }

    private fun drawParticleLine(a: Location, b: Location, particle: Particle) {
        val world = a.world ?: return
        val steps = maxOf(1, (a.distance(b) * 2).toInt())

        for (i in 0..steps) {
            val t = i.toDouble() / steps.toDouble()
            val x = a.x + (b.x - a.x) * t
            val y = a.y + 1.0 + (b.y - a.y) * t
            val z = a.z + (b.z - a.z) * t
            world.spawnParticle(particle, x, y, z, 2, 0.0, 0.0, 0.0, 0.0)
        }
    }
}
