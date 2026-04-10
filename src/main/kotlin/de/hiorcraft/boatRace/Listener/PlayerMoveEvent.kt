package de.hiorcraft.boatRace.Listener

import de.hiorcraft.boatRace.race.RaceManager
import de.hiorcraft.boatRace.race.RaceState
import de.hiorcraft.boatRace.race.RaceTimer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.Location
import kotlin.math.sqrt


class PlayerMoveListener: Listener {

    @EventHandler
    fun onMove(e: PlayerMoveEvent) {
        if (RaceManager.state != RaceState.RUNNING) return

        val track = RaceManager.currentTrack ?: return
        val rp = RaceManager.activePlayers.firstOrNull { it.player == e.player } ?: return
        if (rp.finished) return
        val from = e.from
        val to = e.to

        if (from.world != to.world || to.world != track.lapLineA.world) return

        if (!rp.lapDetectionArmed && distanceToSegmentXZ(to, track.lapLineA, track.lapLineB) > 8.0) {
            rp.lapDetectionArmed = true
        }

        val crossDirection = if (rp.lapDetectionArmed) {
            lapCrossDirection(from, to, track.lapLineA, track.lapLineB)
        } else {
            null
        }

        if (crossDirection != null) {
            val now = System.currentTimeMillis()
            if (now - rp.lastCrossMillis < 1500L) return

            // Eine Runde gilt nur, wenn alle Checkpoints dieser Runde passiert wurden.
            if (track.checkpoints.isNotEmpty() && rp.currentCheckpoint < track.checkpoints.size) {
                return
            }

            if (rp.lapDirectionSign == null) {
                rp.lapDirectionSign = crossDirection
            }
            if (rp.lapDirectionSign != crossDirection) {
                return
            }

            rp.lastCrossMillis = now
            rp.lapDetectionArmed = false
            rp.currentCheckpoint = 0   // Checkpoint-Fortschritt für neue Runde zurücksetzen
            rp.currentRound++

            if (rp.currentRound > RaceManager.totalRounds) {
                rp.finished = true
                rp.totalTime = RaceTimer.stop(rp.player)
                RaceManager.finishPlayer(rp)
            }
        }

        // ── Checkpoint-Erkennung ──────────────────────────────────────────────
        val checkpoints = track.checkpoints
        if (checkpoints.isNotEmpty() && rp.currentCheckpoint < checkpoints.size) {
            val nextCp = checkpoints[rp.currentCheckpoint]
            if (to.world == nextCp.world && to.distance(nextCp) <= CHECKPOINT_RADIUS) {
                rp.currentCheckpoint++
                val passed = rp.currentCheckpoint
                val total  = checkpoints.size
                rp.player.sendActionBar("§a✔ Checkpoint §e$passed§a/§e$total §apasiert!")
            }
        }
    }

    companion object {
        /** Radius in Blöcken, innerhalb dem ein Checkpoint als passiert gilt. */
        const val CHECKPOINT_RADIUS = 4.0
    }

    private fun lapCrossDirection(from: Location, to: Location, a: Location, b: Location): Int? {
        val fromSide = signedSide(from, a, b)
        val toSide = signedSide(to, a, b)

        if (fromSide == 0.0 || toSide == 0.0) return null
        if (fromSide * toSide >= 0.0) return null

        val midX = (from.x + to.x) / 2.0
        val midZ = (from.z + to.z) / 2.0
        val projection = projectionFactorXZ(midX, midZ, a, b)
        if (projection !in -0.15..1.15) return null

        return if (fromSide < 0.0 && toSide > 0.0) 1 else -1
    }

    private fun signedSide(point: Location, a: Location, b: Location): Double {
        return (b.x - a.x) * (point.z - a.z) - (b.z - a.z) * (point.x - a.x)
    }

    private fun projectionFactorXZ(x: Double, z: Double, a: Location, b: Location): Double {
        val abX = b.x - a.x
        val abZ = b.z - a.z
        val lengthSquared = abX * abX + abZ * abZ
        if (lengthSquared == 0.0) return 0.0
        return ((x - a.x) * abX + (z - a.z) * abZ) / lengthSquared
    }

    private fun distanceToSegmentXZ(point: Location, a: Location, b: Location): Double {
        val t = projectionFactorXZ(point.x, point.z, a, b).coerceIn(0.0, 1.0)
        val closestX = a.x + (b.x - a.x) * t
        val closestZ = a.z + (b.z - a.z) * t
        val dx = point.x - closestX
        val dz = point.z - closestZ
        return sqrt(dx * dx + dz * dz)
    }
}