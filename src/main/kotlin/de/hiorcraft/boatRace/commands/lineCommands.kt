package de.hiorcraft.boatRace.commands

import de.hiorcraft.boatRace.Listener.TrackEditingListener
import de.hiorcraft.boatRace.plugin
import de.hiorcraft.boatRace.race.TrackManager
import de.hiorcraft.boatRace.track.TrackEditor
import de.hiorcraft.boatRace.util.DirectionHelper
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument
import org.bukkit.entity.Boat
import org.bukkit.entity.EntityType

private val previewBoats = mutableListOf<Boat>()

fun setLapLineCommand() = commandTree("setline") {
    withAliases("setlapline", "setlineclick")

    stringArgument("map") {
        stringArgument("point") {
            playerExecutor { player, args ->
                val map = args["map"] as String
                val point = (args["point"] as String).uppercase()
                val loc = player.location.block.location.add(0.5, 0.0, 0.5)

                when (point) {
                    "A" -> {
                        TrackEditor.setLapPointA(map, loc)
                        player.sendMessage("§aLinienpunkt A gesetzt.")
                    }

                    "B" -> {
                        val a = TrackEditor.getLapPointA(map)
                        if (a == null) {
                            player.sendMessage("§cBitte zuerst Punkt A setzen: §e/setline $map A")
                            return@playerExecutor
                        }

                        TrackEditor.setLapPointB(map, loc)
                        val dir = DirectionHelper.getDirection(a, loc)
                        val dirText = DirectionHelper.getDirectionSymbol(dir)
                        player.sendMessage("§aLinienpunkt B gesetzt. Start/Ziel-Linie aktiv! $dirText")
                    }

                    else -> player.sendMessage("§cNutze §eA §coder §eB§c: /setline $map <A|B>")
                }
            }
        }

        playerExecutor { player, args ->
            val map = args["map"] as String
            TrackEditingListener.startEditingLapLine(player, map)
            player.sendMessage("§7Setze Punkt A und Punkt B. Diese Linie ist Start und Ziel.")
        }
    }
}

fun setStartLineCommand() = commandTree("setstartline") {
    stringArgument("map") {
        playerExecutor { player, args ->
            val map = args["map"] as String
            TrackEditingListener.startEditingLapLine(player, map)
            player.sendMessage("§7Setze Punkt A und Punkt B (gemeinsame Start/Ziel-Linie).")
        }
    }
}

fun setFinishLineCommand() = commandTree("setfinishline") {
    stringArgument("map") {
        playerExecutor { player, args ->
            val map = args["map"] as String
            TrackEditingListener.startEditingLapLine(player, map)
            player.sendMessage("§7Setze Punkt A und Punkt B (gemeinsame Start/Ziel-Linie).")
        }
    }
}

fun quickSetLapLineCommand() = commandTree("qsetline") {
    withAliases("qsetlapline")

    stringArgument("map") {
        playerExecutor { player, args ->
            val map = args["map"] as String
            val pointA = player.location
            val pointB = pointA.clone().add(player.eyeLocation.direction.multiply(2.0))

            TrackEditor.setLapLine(map, pointA, pointB)

            val dir = DirectionHelper.getDirection(pointA, pointB)
            val dirText = DirectionHelper.getDirectionSymbol(dir)
            player.sendMessage("§aStart/Ziel-Linie schnell gesetzt! $dirText")
        }
    }
}

fun quickSetStartLineCommand() = commandTree("qsetstartline") {
    stringArgument("map") {
        playerExecutor { player, args ->
            val map = args["map"] as String
            val pointA = player.location
            val pointB = pointA.clone().add(player.eyeLocation.direction.multiply(2.0))

            TrackEditor.setLapLine(map, pointA, pointB)

            val dir = DirectionHelper.getDirection(pointA, pointB)
            val dirText = DirectionHelper.getDirectionSymbol(dir)
            player.sendMessage("§aStart/Ziel-Linie schnell gesetzt! $dirText")
        }
    }
}

fun quickSetFinishLineCommand() = commandTree("qsetfinishline") {
    stringArgument("map") {
        playerExecutor { player, args ->
            val map = args["map"] as String
            val pointA = player.location
            val pointB = pointA.clone().add(player.eyeLocation.direction.multiply(2.0))

            TrackEditor.setLapLine(map, pointA, pointB)

            val dir = DirectionHelper.getDirection(pointA, pointB)
            val dirText = DirectionHelper.getDirectionSymbol(dir)
            player.sendMessage("§aStart/Ziel-Linie schnell gesetzt! $dirText")
        }
    }
}

fun previewBoatsCommand() = commandTree("previewboats") {
    stringArgument("map") {
        playerExecutor { player, args ->
            showPreview(player, args["map"] as String, 8)
        }

        integerArgument("seconds") {
            playerExecutor { player, args ->
                val seconds = (args["seconds"] as Int).coerceIn(3, 30)
                showPreview(player, args["map"] as String, seconds)
            }
        }
    }
}

private fun showPreview(player: org.bukkit.entity.Player, map: String, seconds: Int) {
    val track = TrackManager.get(map)

    if (track == null) {
        player.sendMessage("§cMap nicht gefunden: §e$map")
        return
    }

    if (track.startPositions.isEmpty()) {
        player.sendMessage("§cKeine Startpositionen für §e$map§c gesetzt.")
        return
    }

    previewBoats.forEach { if (it.isValid) it.remove() }
    previewBoats.clear()

    for (pos in track.startPositions) {
        val world = pos.world ?: continue
        val spawn = pos.block.location.add(0.5, 0.0, 0.5)
        val boat = world.spawnEntity(spawn, EntityType.OAK_BOAT) as Boat
        boat.isInvulnerable = true
        boat.setGravity(false)
        previewBoats.add(boat)
    }

    player.sendMessage("§aBoot-Preview angezeigt für §e${seconds}s§a.")

    plugin.server.scheduler.runTaskLater(plugin, Runnable {
        previewBoats.forEach { if (it.isValid) it.remove() }
        previewBoats.clear()
    }, seconds * 20L)
}

