package de.hiorcraft.boatRace.race

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.util.Vector

data class RaceTrack(
    val id: String,
    val lapLineA: Location,
    val lapLineB: Location,
    val startPositions: List<Location>,
    val spectator: Location,
    /** Zwischenpunkte entlang der Strecke, in der richtigen Reihenfolge (optionale Checkpoint-Kugeln). */
    val checkpoints: List<Location> = emptyList()
) {
    val startLineA: Location get() = lapLineA
    val startLineB: Location get() = lapLineB
    val finishLineA: Location get() = lapLineA
    val finishLineB: Location get() = lapLineB

    fun direction(): Vector =
        lapLineB.toVector().subtract(lapLineA.toVector()).normalize()
}
