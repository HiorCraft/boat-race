package de.hiorcraft.boatRace.util

import org.bukkit.Location
import kotlin.math.abs

object DirectionHelper {

    enum class Direction {
        NORTH, SOUTH, EAST, WEST, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST
    }

    fun getDirection(from: Location, to: Location): Direction {
        val dx = to.x - from.x
        val dz = to.z - from.z

        return when {
            abs(dx) < 0.1 && dz > 0 -> Direction.SOUTH
            abs(dx) < 0.1 && dz < 0 -> Direction.NORTH
            dx > 0 && abs(dz) < 0.1 -> Direction.EAST
            dx < 0 && abs(dz) < 0.1 -> Direction.WEST
            dx > 0 && dz > 0 -> Direction.SOUTHEAST
            dx > 0 && dz < 0 -> Direction.NORTHEAST
            dx < 0 && dz > 0 -> Direction.SOUTHWEST
            else -> Direction.NORTHWEST
        }
    }

    fun getDirectionSymbol(direction: Direction): String {
        return when (direction) {
            Direction.NORTH -> "↑ Nord"
            Direction.SOUTH -> "↓ Süd"
            Direction.EAST -> "→ Ost"
            Direction.WEST -> "← West"
            Direction.NORTHEAST -> "↗ Nordost"
            Direction.NORTHWEST -> "↖ Nordwest"
            Direction.SOUTHEAST -> "↘ Südost"
            Direction.SOUTHWEST -> "↙ Südwest"
        }
    }
}

