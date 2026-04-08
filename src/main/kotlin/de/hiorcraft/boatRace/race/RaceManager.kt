package de.hiorcraft.boatRace.race

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

object RaceManager {
    val queue = mutableListOf<Player>()
    val activePlayers = mutableListOf<RacePlayer>()
    var state = RaceState.WAITING
    var currentTrack: RaceTrack? = null
    var totalRounds = 3

    fun isInQueue(player: Player): Boolean = queue.contains(player)

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

    fun startRace(track: RaceTrack) {
        currentTrack = track
        state = RaceState.RUNNING
        activePlayers.clear()

        for ((index, player) in queue.withIndex()) {
            activePlayers.add(RacePlayer(player, index))
        }

        queue.clear()
    }

    fun finishPlayer(racePlayer: RacePlayer) {
        racePlayer.player.sendMessage("§a§lDu hast das Rennen beendet!")

        // Überprüfe ob alle Spieler fertig sind
        if (activePlayers.all { it.finished }) {
            endRace()
        }
    }

    fun endRace() {
        finishRace()
        state = RaceState.WAITING
        activePlayers.clear()
        currentTrack = null
    }
}

object TrackManager {

    private val tracks = mutableMapOf<String, RaceTrack>()

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

    fun getAll() = tracks.values

    private fun parseLoc(s: String): Location {
        val p = s.split(",")
        val world = Bukkit.getWorld(p[0])!!
        return Location(world, p[1].toDouble(), p[2].toDouble(), p[3].toDouble())
    }

}
