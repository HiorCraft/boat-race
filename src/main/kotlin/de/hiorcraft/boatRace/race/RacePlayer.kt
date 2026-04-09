package de.hiorcraft.boatRace.race

import org.bukkit.entity.Player

data class RacePlayer(
    val player: Player,
    val startIndex: Int,
    var currentRound: Int = 1,
    /** Index des nächsten Checkpoints, den der Spieler passieren muss (0 = noch keinen passiert). */
    var currentCheckpoint: Int = 0,
    var finished: Boolean = false,
    var totalTime: Double = 0.0,
    var lapDetectionArmed: Boolean = false,
    var lastCrossMillis: Long = 0L,
)
