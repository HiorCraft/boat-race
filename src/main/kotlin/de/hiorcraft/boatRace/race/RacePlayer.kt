package de.hiorcraft.boatRace.race

import org.bukkit.entity.Player

data class RacePlayer(
    val player: Player,
    val startIndex: Int,
    var currentRound: Int = 1,
    var finished: Boolean = false,
    var totalTime: Double = 0.0,
)
