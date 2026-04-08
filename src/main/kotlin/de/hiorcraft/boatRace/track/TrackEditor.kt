package de.hiorcraft.boatRace.track

import de.hiorcraft.boatRace.plugin
import de.hiorcraft.boatRace.race.TrackManager
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object TrackEditor {

    private val file = File("plugins/BoatRace/tracks.yml")
    private val yml = YamlConfiguration.loadConfiguration(file)

    fun createMap(id: String): Boolean {
        val base = "tracks.$id"

        if (yml.contains(base)) return false

        yml.set("$base.startLine.a", "")
        yml.set("$base.startLine.b", "")
        yml.set("$base.finishLine.a", "")
        yml.set("$base.finishLine.b", "")
        yml.set("$base.startPositions", mutableListOf<String>())
        yml.set("$base.spectator", "")

        save()
        return true
    }

    fun setStartLine(id: String, a: Location, b: Location) {
        val base = "tracks.$id.startLine"
        yml.set("$base.a", serialize(a))
        yml.set("$base.b", serialize(b))
        save()
    }

    fun setFinishLine(id: String, a: Location, b: Location) {
        val base = "tracks.$id.finishLine"
        yml.set("$base.a", serialize(a))
        yml.set("$base.b", serialize(b))
        save()
    }

    fun addStartPosition(id: String, loc: Location) {
        val list = yml.getStringList("tracks.$id.startPositions")
        list.add(serialize(loc))
        yml.set("tracks.$id.startPositions", list)
        save()
    }

    fun setStartPosition(id: String, loc: Location) {
        val list = yml.getStringList("tracks.$id.startPositions")
        list.clear()
        list.add(serialize(loc))
        yml.set("tracks.$id.startPositions", list)
        save()
    }

    fun setSpectator(id: String, loc: Location) {
        yml.set("tracks.$id.spectator", serialize(loc))
        save()
    }

    private fun serialize(loc: Location): String =
        "${loc.world.name},${loc.x},${loc.y},${loc.z}"

    private fun save() {
        yml.save(file)
        TrackManager.load(plugin)
    }
}
