package de.hiorcraft.boatRace.track

import de.hiorcraft.boatRace.plugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

data class TrackValidationResult(
    val errors: List<String>,
    val warnings: List<String>
)

object TrackValidationService {

    fun validate(mapId: String): TrackValidationResult {
        val file = File(plugin.dataFolder, "tracks.yml")
        val yml = YamlConfiguration.loadConfiguration(file)
        val base = "tracks.$mapId"

        if (!yml.contains(base)) {
            return TrackValidationResult(
                errors = listOf("Map '$mapId' existiert nicht in tracks.yml."),
                warnings = emptyList()
            )
        }

        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        val lapARaw = yml.getString("$base.lapLine.a")
            ?: yml.getString("$base.startLine.a")
            ?: yml.getString("$base.finishLine.a")
        val lapBRaw = yml.getString("$base.lapLine.b")
            ?: yml.getString("$base.startLine.b")
            ?: yml.getString("$base.finishLine.b")

        val lapA = parseLoc(lapARaw)
        val lapB = parseLoc(lapBRaw)
        val spectator = parseLoc(yml.getString("$base.spectator"))
        val starts = yml.getStringList("$base.startPositions").mapNotNull { parseLoc(it) }

        if (lapA == null) errors.add("LapLine Punkt A fehlt oder ist ungültig.")
        if (lapB == null) errors.add("LapLine Punkt B fehlt oder ist ungültig.")
        if (spectator == null) errors.add("Spectator-Position fehlt oder ist ungültig.")
        if (starts.isEmpty()) errors.add("Mindestens 1 Startposition fehlt.")

        if (lapA != null && lapB != null) {
            if (lapA.world?.name != lapB.world?.name) {
                errors.add("LapLine A und B sind in unterschiedlichen Welten.")
            }
            if (lapA.distanceSquared(lapB) < 1.0) {
                warnings.add("LapLine ist sehr kurz (< 1 Block Distanz).")
            }
        }

        if (lapA != null) {
            starts.forEachIndexed { index, start ->
                if (start.world?.name != lapA.world?.name) {
                    errors.add("Startposition ${index + 1} ist nicht in der gleichen Welt wie die LapLine.")
                }
            }
            if (spectator != null && spectator.world?.name != lapA.world?.name) {
                warnings.add("Spectator ist in anderer Welt als LapLine.")
            }
        }

        return TrackValidationResult(errors, warnings)
    }

    private fun parseLoc(raw: String?): Location? {
        if (raw.isNullOrBlank()) return null
        val p = raw.split(",")
        if (p.size < 4) return null

        val world = Bukkit.getWorld(p[0]) ?: return null
        val x = p[1].toDoubleOrNull() ?: return null
        val y = p[2].toDoubleOrNull() ?: return null
        val z = p[3].toDoubleOrNull() ?: return null
        val yaw = p.getOrNull(4)?.toFloatOrNull() ?: 0f
        return Location(world, x, y, z, yaw, 0f)
    }
}
