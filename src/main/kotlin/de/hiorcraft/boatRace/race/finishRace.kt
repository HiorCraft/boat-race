package de.hiorcraft.boatRace.race

import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.entity.Firework


fun finishRace() {
    val track = RaceManager.currentTrack ?: return
    val sorted = RaceManager.activePlayers.sortedBy { it.totalTime }

    val podium = listOf(
        Location(track.spectator.world, track.spectator.x, track.spectator.y + 5.0, track.spectator.z),   // Platz 1
        Location(track.spectator.world, track.spectator.x - 2.0, track.spectator.y + 4.0, track.spectator.z),  // Platz 2
        Location(track.spectator.world, track.spectator.x + 2.0, track.spectator.y + 4.0, track.spectator.z)    // Platz 3
    )

    sorted.take(3).forEachIndexed { index, rp ->
        rp.player.teleport(podium[index])
        spawnFireworks(podium[index])
        rp.player.sendTitle("§6Platz ${index + 1}", "§e${"%.2f".format(rp.totalTime)}s", 10, 60, 20)
    }
}

fun spawnFireworks(loc: Location) {
    val fw = loc.world!!.spawn(loc, Firework::class.java)
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
