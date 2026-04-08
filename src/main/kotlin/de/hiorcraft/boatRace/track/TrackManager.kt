package de.hiorcraft.boatRace.track

import de.hiorcraft.boatRace.race.RaceTrack
import de.hiorcraft.boatRace.race.TrackManager as RaceTrackManager
import org.bukkit.plugin.java.JavaPlugin

object TrackManager {

    fun load(plugin: JavaPlugin) {
        RaceTrackManager.load(plugin)
    }

    fun get(id: String): RaceTrack? = RaceTrackManager.get(id)

    fun getAll(): Collection<RaceTrack> = RaceTrackManager.getAll()

    fun reload(plugin: JavaPlugin) = RaceTrackManager.load(plugin)
}