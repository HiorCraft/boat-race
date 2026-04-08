package de.hiorcraft.boatRace.track

import de.hiorcraft.boatRace.plugin
import de.hiorcraft.boatRace.race.TrackManager
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

object TrackEditor {

    private val file = File(plugin.dataFolder, "tracks.yml")
    private val yml = YamlConfiguration.loadConfiguration(file)

    fun createMap(id: String): Boolean {
        val base = "tracks.$id"

        if (yml.contains(base)) return false

        yml.set("$base.lapLine.a", "")
        yml.set("$base.lapLine.b", "")
        yml.set("$base.startPositions", mutableListOf<String>())
        yml.set("$base.spectator", "")

        save()
        return true
    }

    fun setStartLine(id: String, a: Location, b: Location) {
        setLapLine(id, a, b)
    }

    fun setFinishLine(id: String, a: Location, b: Location) {
        setLapLine(id, a, b)
    }

    fun setLapLine(id: String, a: Location, b: Location) {
        val base = "tracks.$id.lapLine"
        yml.set("$base.a", serialize(a))
        yml.set("$base.b", serialize(b))

        syncLegacyLineKeys(id, a, b)
        save()
    }

    fun setLapPointA(id: String, a: Location) {
        yml.set("tracks.$id.lapLine.a", serialize(a))
        syncLegacyLineKeysIfComplete(id)
        save()
    }

    fun setLapPointB(id: String, b: Location) {
        yml.set("tracks.$id.lapLine.b", serialize(b))
        syncLegacyLineKeysIfComplete(id)
        save()
    }

    fun getLapPointA(id: String): Location? = parse(yml.getString("tracks.$id.lapLine.a"))

    fun getLapPointB(id: String): Location? = parse(yml.getString("tracks.$id.lapLine.b"))

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

    private fun parse(raw: String?): Location? {
        if (raw.isNullOrBlank()) return null
        val p = raw.split(",")
        if (p.size < 4) return null
        val world = plugin.server.getWorld(p[0]) ?: return null
        return Location(world, p[1].toDoubleOrNull() ?: return null, p[2].toDoubleOrNull() ?: return null, p[3].toDoubleOrNull() ?: return null)
    }

    private fun syncLegacyLineKeysIfComplete(id: String) {
        val a = getLapPointA(id) ?: return
        val b = getLapPointB(id) ?: return
        syncLegacyLineKeys(id, a, b)
    }

    private fun syncLegacyLineKeys(id: String, a: Location, b: Location) {
        yml.set("tracks.$id.startLine.a", serialize(a))
        yml.set("tracks.$id.startLine.b", serialize(b))
        yml.set("tracks.$id.finishLine.a", serialize(a))
        yml.set("tracks.$id.finishLine.b", serialize(b))
    }

    private fun save() {
        yml.save(file)
        TrackManager.load(plugin)
    }
}
