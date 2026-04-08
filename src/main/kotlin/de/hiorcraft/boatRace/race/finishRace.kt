package de.hiorcraft.boatRace.race

import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Location


fun finishRace() {
    val sorted = racePlayers.sortedBy { it.totalTime }

    val podium = listOf(
        Location(world, 0.0, 105.0, 0.0),   // Platz 1
        Location(world, -2.0, 104.0, 0.0),  // Platz 2
        Location(world, 2.0, 104.0, 0.0)    // Platz 3
    )

    sorted.take(3).forEachIndexed { index, rp ->
        rp.player.teleport(podium[index])
        spawnFireworks(podium[index])
        rp.player.sendTitle("§6Platz ${index + 1}", "§e${"%.2f".format(rp.totalTime)}s", 10, 60, 20)
    }
}

fun spawnFireworks(loc: Location) {
    val fw = loc.world.spawn(loc, Firework::class.java)
    val meta = fw.fireworkMeta
    meta.power = 1
    meta.addEffect(
        FireworkEffect.builder()
            .withColor(Color.AQUA)
            .with(FireworkEffect.Type.BALL_LARGE)
            .flicker(true)
            .trail(true)
            .build()
    )
    fw.fireworkMeta = meta
}
