package de.hiorcraft.boatRace.race

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object TrackManager {

    private val tracks = mutableMapOf<String, RaceTrack>()
    val queue = mutableListOf<Player>()
    var state = RaceState.WAITING

    fun load(plugin: JavaPlugin) {
        val file = File(plugin.dataFolder, "tracks.yml")
        if (!file.exists()) plugin.saveResource("tracks.yml", false)

        val yml = YamlConfiguration.loadConfiguration(file)
        val section = yml.getConfigurationSection("tracks") ?: return

        for (id in section.getKeys(false)) {
            val base = "tracks.$id"

            val startA = parseLoc(yml.getString("$base.startLine.a")!!)
            val startB = parseLoc(yml.getString("$base.startLine.b")!!)
            val finishA = parseLoc(yml.getString("$base.finishLine.a")!!)
            val finishB = parseLoc(yml.getString("$base.finishLine.b")!!)
            val spectator = parseLoc(yml.getString("$base.spectator")!!)

            val starts = yml.getStringList("$base.startPositions").map { parseLoc(it) }

            tracks[id] = RaceTrack(
                id,
                startA, startB,
                finishA, finishB,
                starts,
                spectator
            )
        }
    }

    fun get(id: String) = tracks[id]

    private fun parseLoc(s: String): Location {
        val p = s.split(",")
        val world = Bukkit.getWorld(p[0])!!
        return Location(world, p[1].toDouble(), p[2].toDouble(), p[3].toDouble())
    }

    fun join(player: Player): Boolean {
        if (state != RaceState.WAITING) {
            player.sendMessage("§cDas Rennen läuft bereits!")
            return false
        }

        if (queue.contains(player)) {
            player.sendMessage("§eDu bist bereits in der Queue.")
            return false
        }

        queue.add(player)
        player.sendMessage("§aDu bist der Queue beigetreten! (${queue.size} Spieler)")
        return true
    }

    fun leave(player: Player) {
        queue.remove(player)
        player.sendMessage("§cDu hast die Queue verlassen.")
    }

    fun isInQueue(player: Player): Boolean = queue.contains(player)
}


