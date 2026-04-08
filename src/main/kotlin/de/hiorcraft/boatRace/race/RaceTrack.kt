package de.hiorcraft.boatRace.race

import org.bukkit.Bukkit
import org.bukkit.Location

data class RaceTrack(
    val startPoint: Liste<Location>,
    val finishLine: Location
) {
    companion object {
        fun default(): RaceTrack {
            val world = Bukkit.getWorld("world")!!
            return RaceTrack(
                startPoint = listOf(
                    Location(world, 100.0, 65.0, 100.0),
                    Location(world, 102.0, 65.0, 100.0),
                    Location(world, 104.0, 65.0, 100.0),
                    Location(world, 106.0, 65.0, 100.0)
                ),
                finishLine = Location(world[0], 200.0, 65.0, 100.0)
            )
        }
    }
}
