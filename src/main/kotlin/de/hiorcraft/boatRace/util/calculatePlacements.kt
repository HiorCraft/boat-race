package de.hiorcraft.boatRace.util

import de.hiorcraft.boatRace.race.RacePlayer

fun calculatePlacements(): List<RacePlayer> {
    val track = currentTrack ?: return racePlayers

    return racePlayers.sortedWith(
        compareByDescending<RacePlayer> { it.currentRound }
            .thenByDescending { progressOnTrack(it.player.location, track.startLineA, track.finishLine) }
    )
}
