package de.hiorcraft.boatRace.util

import de.hiorcraft.boatRace.race.RaceManager
import de.hiorcraft.boatRace.race.RacePlayer
import de.hiorcraft.boatRace.race.RaceTrack

/**
 * Berechnet die aktuelle Rennplatzierung.
 *
 * Sortierkriterien (absteigend = besser):
 *  1. Fertig gefahrene Spieler zuerst, sortiert nach Gesamtzeit (aufsteigend)
 *  2. Noch fahrende Spieler: aktuelle Runde (höher = weiter vorne)
 *  3. Anzahl passierter Checkpoints in der aktuellen Runde
 *  4. Abstand zum nächsten Checkpoint / zur Ziellinie (kleiner = weiter vorne)
 */
fun calculatePlacements(): List<RacePlayer> {
    val track = RaceManager.currentTrack ?: return emptyList()

    val (finished, racing) = RaceManager.activePlayers.partition { it.finished }

    // Fertige Spieler: kürzeste Gesamtzeit zuerst
    val finishedSorted = finished.sortedBy { it.totalTime }

    // Noch fahrende Spieler: nach Runde → Checkpoints → Abstand
    val racingSorted = racing.sortedWith(
        compareByDescending<RacePlayer> { it.currentRound }
            .thenByDescending { it.currentCheckpoint }
            .thenBy { distanceToNextTarget(it, track) }
    )

    return finishedSorted + racingSorted
}

/**
 * Gibt den Abstand des Spielers zum nächsten Ziel zurück (niedrigerer Wert = weiter vorne).
 * - Gibt es noch offene Checkpoints: Abstand zum nächsten Checkpoint.
 * - Alle Checkpoints passiert (oder keine vorhanden): restliche Strecke bis zur Ziellinie.
 */
private fun distanceToNextTarget(rp: RacePlayer, track: RaceTrack): Double {
    val checkpoints = track.checkpoints
    val loc = rp.player.location

    return if (checkpoints.isNotEmpty() && rp.currentCheckpoint < checkpoints.size) {
        val cp = checkpoints[rp.currentCheckpoint]
        if (loc.world == cp.world) loc.distance(cp) else Double.MAX_VALUE
    } else {
        // Fortschritt auf der Linie 0..1 → restliche Strecke
        val progress = progressOnTrack(loc, track.lapLineA, track.lapLineB)
        (1.0 - progress) * track.lapLineA.distance(track.lapLineB)
    }
}
