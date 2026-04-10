package de.hiorcraft.boatRace.race

import de.hiorcraft.boatRace.plugin
import de.hiorcraft.boatRace.util.BoatSpawner
import de.hiorcraft.boatRace.util.ChatConfig
import de.hiorcraft.boatRace.util.FinishLineVisualizer
import de.hiorcraft.boatRace.util.RaceScoreboard
import org.bukkit.Bukkit
import org.bukkit.GameMode
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
    private const val DEFAULT_MAX_PLAYERS = 8
    var lobbyLocation: Location? = null
    var podiumBaseLocation: Location? = null

    val maxPlayers: Int
        get() = plugin.config.getInt("queue.maxPlayers", DEFAULT_MAX_PLAYERS).coerceAtLeast(1)

    fun isInQueue(player: Player): Boolean = queue.contains(player)

    fun join(player: Player): Boolean {
        if (state != RaceState.WAITING) {
            player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.ERROR}Das Rennen läuft bereits!")
            return false
        }

        if (queue.contains(player)) {
            player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.WARNING}Du bist bereits in der Queue.")
            return false
        }

        if (queue.size >= maxPlayers) {
            player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.ERROR}Die Queue ist voll (${queue.size}/$maxPlayers).")
            return false
        }

        queue.add(player)
        player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.SUCCESS}Du bist der Queue beigetreten! (${queue.size}/$maxPlayers)")
        return true
    }

    fun leave(player: Player) {
        queue.remove(player)
        player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.ERROR}Du hast die Queue verlassen.")
    }

    fun leaveActiveRace(player: Player): Boolean {
        val racePlayer = activePlayers.firstOrNull { it.player == player } ?: return false

        RaceTimer.stop(player)
        RaceScoreboard.removeBoard(player)

        val vehicle = player.vehicle
        if (vehicle != null) {
            player.leaveVehicle()
            vehicle.remove()
        }

        activePlayers.remove(racePlayer)

        val lobby = lobbyLocation
        if (lobby != null) {
            player.gameMode = GameMode.ADVENTURE
            player.teleport(lobby)
        }


        return true
    }

    fun startRace(track: RaceTrack) {
        currentTrack = track
        state = RaceState.COUNTDOWN
        activePlayers.clear()

        for ((index, player) in queue.withIndex()) {
            activePlayers.add(RacePlayer(player, index))
        }

        queue.clear()
    }

    fun finishPlayer(racePlayer: RacePlayer) {
        racePlayer.player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.SUCCESS}Du hast das Rennen beendet!")

        val vehicle = racePlayer.player.vehicle
        if (vehicle != null) {
            racePlayer.player.leaveVehicle()
            vehicle.remove()
        }

        val spectator = currentTrack?.spectator
        if (spectator != null) {
            racePlayer.player.teleport(spectator)
            racePlayer.player.gameMode = GameMode.SPECTATOR
            racePlayer.player.sendActionBar("§7Du bist jetzt Zuschauer.")
        }

        // Überprüfe ob alle Spieler fertig sind
        if (activePlayers.all { it.finished }) {
            endRace()
        }
    }

    fun endRace(showPodium: Boolean = true) {
        val players = activePlayers.map { it.player }
        FinishLineVisualizer.stopDisplayingLapLine()
        RaceScoreboard.stopAutoUpdate()
        BoatSpawner.clearSpawnedBoats()
        for (racePlayer in activePlayers) {
            RaceScoreboard.removeBoard(racePlayer.player)
        }

        if (showPodium) {
            finishRace()
        }

        val lobby = lobbyLocation
        if (!showPodium && lobby != null) {
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                for (player in players) {
                    if (player.isInsideVehicle) {
                        player.leaveVehicle()
                    }
                    player.gameMode = GameMode.ADVENTURE
                    player.teleport(lobby)
                }
            }, 0L)
        }

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
        RaceManager.lobbyLocation = parseLoc(plugin.config.getString("locations.lobby"))
            ?: parseLoc(yml.getString("lobby"))
        RaceManager.podiumBaseLocation = parseLoc(plugin.config.getString("locations.podiumBase"))
        tracks.clear()
        val section = yml.getConfigurationSection("tracks") ?: return

        for (id in section.getKeys(false)) {
            val base = "tracks.$id"
            val lapA = yml.getString("$base.lapLine.a") ?: yml.getString("$base.startLine.a") ?: yml.getString("$base.finishLine.a")
            val lapB = yml.getString("$base.lapLine.b") ?: yml.getString("$base.startLine.b") ?: yml.getString("$base.finishLine.b")
            val spectator = yml.getString("$base.spectator")

            // Skip incomplete tracks
            if (lapA.isNullOrEmpty() || lapB.isNullOrEmpty() ||
                spectator.isNullOrEmpty()) {
                continue
            }

            val lapLineA = parseLoc(lapA)
            val lapLineB = parseLoc(lapB)
            val spectatorLoc = parseLoc(spectator)
            if (lapLineA == null || lapLineB == null || spectatorLoc == null) {
                plugin.logger.warning("Track '$id' wurde übersprungen: ungültige Pflicht-Location in tracks.yml")
                continue
            }

            val starts = yml.getStringList("$base.startPositions").mapNotNull {
                if (it.isNotEmpty()) parseLoc(it) else null
            }

            val checkpoints = yml.getStringList("$base.checkpoints").mapNotNull {
                if (it.isNotEmpty()) parseLoc(it) else null
            }

            tracks[id] = RaceTrack(
                id,
                lapLineA, lapLineB,
                starts,
                spectatorLoc,
                checkpoints
            )
        }
    }

    fun get(id: String) = tracks[id]

    fun getAll() = tracks.values

    private fun parseLoc(s: String?): Location? {
        if (s.isNullOrBlank()) return null
        val p = s.split(",")
        if (p.size < 4) return null

        val world = Bukkit.getWorld(p[0]) ?: return null
        val x = p[1].toDoubleOrNull() ?: return null
        val y = p[2].toDoubleOrNull() ?: return null
        val z = p[3].toDoubleOrNull() ?: return null
        val yaw = p.getOrNull(4)?.toFloatOrNull() ?: 0f
        return Location(world, x, y, z, yaw, 0f)
    }

}
