package de.hiorcraft.boatRace.util

import de.hiorcraft.boatRace.plugin
import de.hiorcraft.boatRace.race.RaceManager
import de.hiorcraft.boatRace.race.RacePlayer
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Boat
import org.bukkit.entity.EntityType
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scheduler.BukkitRunnable
import java.util.UUID

object RaceCountdown {

    fun startCountdown(onStart: () -> Unit) {
        startCountdown(3, "§7Start in", "§a§lGO!", "", onStart)
    }

    fun startLobbyAndMapCountdown(
        lobbySeconds: Int = 10,
        mapSeconds: Int = 10,
        startSeconds: Int = 3,
        onMapTeleport: () -> Unit,
        onStart: () -> Unit
    ) {
        startCountdown(lobbySeconds, "§7Map-Teleport in", "§bTeleport", "§7Ab zur Strecke") {
            onMapTeleport()
            startCountdown(mapSeconds, "§7Rennen startet in", "§eBereit?", "§7Boot-Start folgt") {
                startCountdown(startSeconds, "§7Start in", "§a§lGO!", "", onStart)
            }
        }
    }

    private fun startCountdown(
        seconds: Int,
        subtitlePrefix: String,
        doneTitle: String,
        doneSubtitle: String,
        onDone: () -> Unit
    ) {
        var count = seconds.coerceAtLeast(1)

        for (player in RaceManager.activePlayers) {
            player.player.sendTitle("§e$count", "$subtitlePrefix §e${count}s", 0, 20, 0)
        }

        object : BukkitRunnable() {
            override fun run() {
                count--

                if (count > 0) {
                    for (player in RaceManager.activePlayers) {
                        player.player.sendTitle("§e$count", "$subtitlePrefix §e${count}s", 0, 20, 0)
                        player.player.playSound(player.player.location, org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f)
                    }
                } else {
                    for (player in RaceManager.activePlayers) {
                        player.player.sendTitle(doneTitle, doneSubtitle, 0, 20, 10)
                        player.player.playSound(player.player.location, org.bukkit.Sound.BLOCK_DISPENSER_DISPENSE, 1f, 1f)
                    }
                    cancel()
                    onDone()
                }
            }
        }.runTaskTimer(plugin, 0L, 20L)
    }
}

object BoatSpawner {

    private val spawnedRaceBoats = mutableListOf<Boat>()
    private val boatsByPlayer = mutableMapOf<UUID, Boat>()

    fun clearSpawnedBoats() {
        boatsByPlayer.values.forEach { if (it.isValid) it.remove() }
        spawnedRaceBoats.forEach { if (it.isValid) it.remove() }
        boatsByPlayer.clear()
        spawnedRaceBoats.clear()
    }

    fun spawnBoatsAtStartPositions() {
        val track = RaceManager.currentTrack ?: return
        if (track.startPositions.isEmpty()) return
        clearSpawnedBoats()

        for ((index, racePlayer) in RaceManager.activePlayers.withIndex()) {
            val baseLoc = track.getGridStartPosition(index) ?: continue
            val spawnLoc = baseLoc.block.location.add(0.5, 0.0, 0.5).apply {
                yaw = baseLoc.yaw
                pitch = 0f
            }
            spawnBoatFor(racePlayer, spawnLoc)
        }
    }

    fun respawnBoatFor(racePlayer: RacePlayer, at: Location? = null) {
        val player = racePlayer.player
        val base = (at ?: player.location).clone().apply { pitch = 0f }
        if (player.isInsideVehicle) {
            player.leaveVehicle()
        }
        spawnBoatFor(racePlayer, base.add(0.0, 0.1, 0.0))
    }

    private fun spawnBoatFor(racePlayer: RacePlayer, spawnLoc: Location) {
        val world = spawnLoc.world ?: return

        if (!world.isChunkLoaded(spawnLoc.blockX shr 4, spawnLoc.blockZ shr 4)) {
            world.loadChunk(spawnLoc.blockX shr 4, spawnLoc.blockZ shr 4)
        }

        val player = racePlayer.player
        boatsByPlayer.remove(player.uniqueId)?.let {
            if (it.isValid) it.remove()
            spawnedRaceBoats.remove(it)
        }

        player.teleport(spawnLoc)

        val boat = world.spawnEntity(spawnLoc, EntityType.OAK_BOAT) as Boat
        spawnedRaceBoats.add(boat)
        boatsByPlayer[player.uniqueId] = boat

        if (!boat.addPassenger(player)) {
            player.teleport(boat.location)
            boat.addPassenger(player)
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
