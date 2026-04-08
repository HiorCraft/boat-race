package de.hiorcraft.boatRace.util

import org.bukkit.Location

fun progressOnTrack(playerLoc: Location, start: Location, finish: Location): Double {
    val a = start.toVector()
    val b = finish.toVector()
    val p = playerLoc.toVector()

    val ab = b.clone().subtract(a)
    val ap = p.clone().subtract(a)

    val t = ap.dot(ab) / ab.lengthSquared()

    return t.coerceIn(0.0, 1.0)
}
