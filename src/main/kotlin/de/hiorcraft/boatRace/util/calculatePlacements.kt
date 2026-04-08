package de.hiorcraft.boatRace.util

import de.hiorcraft.boatRace.race.RaceManager
import de.hiorcraft.boatRace.race.RacePlayer

fun calculatePlacements(): List<RacePlayer> {
    val track = RaceManager.currentTrack ?: return emptyList()

    return RaceManager.activePlayers.sortedWith(
        compareByDescending<RacePlayer> { it.currentRound }
            .thenByDescending { progressOnTrack(it.player.location, track.lapLineA, track.lapLineB) }
    )
}
