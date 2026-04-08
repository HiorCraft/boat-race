package de.hiorcraft.boatRace.race

import org.bukkit.Bukkit
import org.bukkit.Location

data class RaceTrack(
    val id: String,
    val startLineA: Location,
    val startLineB: Location,
    val finishLineA: Location,
    val finishLineB: Location,
    val startPositions: List<Location>,
    val spectator: Location
) {
    companion object {
        fun default(): RaceTrack {
            val world = Bukkit.getWorld("world")!!
            return RaceTrack(
                id = "default",
                startLineA = Location(world, 100.0, 65.0, 100.0),
                startLineB = Location(world, 100.0, 65.0, 102.0),
                finishLineA = Location(world, 200.0, 65.0, 100.0),
                finishLineB = Location(world, 200.0, 65.0, 102.0),
                startPositions = listOf(
                    Location(world, 100.0, 65.0, 100.0),
                    Location(world, 102.0, 65.0, 100.0),
                    Location(world, 104.0, 65.0, 100.0),
                    Location(world, 106.0, 65.0, 100.0)
                ),
                spectator = Location(world, 150.0, 70.0, 100.0)
            )
        }
    }
}
