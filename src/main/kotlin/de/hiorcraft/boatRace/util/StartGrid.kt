package de.hiorcraft.boatRace.util

import de.hiorcraft.boatRace.race.RaceTrack
import org.bukkit.Location
import org.bukkit.util.Vector

private const val GRID_ROW_SPACING = 3.0

fun RaceTrack.getGridStartPosition(index: Int): Location? {
    if (index < 0 || startPositions.isEmpty()) return null

    if (startPositions.size == 1) {
        return startPositions[0].clone().add(backwardOffset(startPositions[0], index))
    }

    val laneBase = if (index % 2 == 0) startPositions[0] else startPositions[1]
    val row = index / 2
    return laneBase.clone().add(backwardOffset(laneBase, row))
}

fun RaceTrack.getGridStartPositions(count: Int): List<Location> =
    (0 until count).mapNotNull { getGridStartPosition(it) }

private fun backwardOffset(base: Location, row: Int): Vector {
    if (row <= 0) return Vector(0, 0, 0)

    val forward = base.direction.clone().setY(0.0)
    if (forward.lengthSquared() < 1.0E-6) return Vector(0, 0, 0)

    return forward.normalize().multiply(-GRID_ROW_SPACING * row)
}

