package de.hiorcraft.boatRace.commands

import de.hiorcraft.boatRace.Listener.TrackEditingListener
import de.hiorcraft.boatRace.race.RaceManager
import de.hiorcraft.boatRace.track.TrackEditor
import de.hiorcraft.boatRace.util.ChatConfig
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument

fun addStartPosCommand() = commandTree("addstartpos") {
    stringArgument("map") {
        playerExecutor { player, args ->
            val map = args["map"] as String
            TrackEditingListener.startEditingStartPos(player, map)
        }
    }
}

fun setSpectatorCommand() = commandTree("setspectator") {
    stringArgument("map") {
        playerExecutor { player, args ->
            val map = args["map"] as String
            TrackEditingListener.startEditingSpectator(player, map)
        }
    }
}

fun setLobbyCommand() = commandTree("setlobby") {
    playerExecutor { player, _ ->
        val loc = player.location.clone().apply { pitch = 0f }
        TrackEditor.setLobby(loc)
        RaceManager.lobbyLocation = loc
        player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.SUCCESS}Lobby gesetzt!")
    }
}

fun lobbyCommand() = commandTree("lobby") {
    playerExecutor { player, _ ->
        val lobby = RaceManager.lobbyLocation
        if (lobby == null) {
            player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.ERROR}Lobby ist nicht gesetzt. Nutze §e/setlobby")
            return@playerExecutor
        }
        player.teleport(lobby)
        player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.SUCCESS}Zur Lobby teleportiert.")
    }
}

fun setPodestCommand() = commandTree("setpodest") {
    withAliases("setpodium", "setpodestcoord")
    playerExecutor { player, _ ->
        val loc = player.location.clone().apply { pitch = 0f }
        TrackEditor.setPodiumBase(loc)
        RaceManager.podiumBaseLocation = loc
        player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.SUCCESS}Podest-Basis gesetzt (config.yml)!")
    }
}

// ── Checkpoint-Commands ───────────────────────────────────────────────────────

/**
 * /addcheckpoint <map>        → Aktiviert Block-Klick-Modus (Rechtsklick = CP setzen, Linksklick = fertig)
 * /addcheckpoint stop <map>   → Beendet den Modus manuell
 */
fun addCheckpointCommand() = commandTree("addcheckpoint") {
    withAliases("addcp")

    // /addcheckpoint stop <map>
    stringArgument("action") {
        stringArgument("map") {
            playerExecutor { player, args ->
                val action = (args["action"] as String).lowercase()
                val map    = args["map"] as String

                if (action == "stop") {
                    TrackEditingListener.stopEditingCheckpoint(player)
                } else {
                    // /addcheckpoint <map> – "action" ist tatsächlich der Map-Name
                    TrackEditingListener.startEditingCheckpoint(player, action)
                }
            }
        }

        // /addcheckpoint <map> ohne zweites Argument → action = map-Name
        playerExecutor { player, args ->
            val map = args["action"] as String
            TrackEditingListener.startEditingCheckpoint(player, map)
        }
    }
}

/**
 * /removecheckpoint <map>
 * Entfernt den zuletzt gesetzten Checkpoint (Undo).
 */
fun removeCheckpointCommand() = commandTree("removecheckpoint") {
    withAliases("removecp", "undocheckpoint")

    stringArgument("map") {
        playerExecutor { player, args ->
            val map = args["map"] as String
            val removed = TrackEditor.removeLastCheckpoint(map)
            if (removed) {
                val remaining = TrackEditor.getCheckpoints(map).size
                player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.SUCCESS}Letzter Checkpoint entfernt. Noch §e$remaining §7übrig.")
            } else {
                player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.WARNING}Keine Checkpoints für §e$map§e gesetzt.")
            }
        }
    }
}

/**
 * /clearcheckpoints <map>
 * Löscht alle Checkpoints einer Map.
 */
fun clearCheckpointsCommand() = commandTree("clearcheckpoints") {
    withAliases("clearcp")

    stringArgument("map") {
        playerExecutor { player, args ->
            val map = args["map"] as String
            TrackEditor.clearCheckpoints(map)
            player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.SUCCESS}Alle Checkpoints für §e$map §agelöscht.")
        }
    }
}

/**
 * /listcheckpoints <map>
 * Zeigt alle gesetzten Checkpoints einer Map an.
 */
fun listCheckpointsCommand() = commandTree("listcheckpoints") {
    withAliases("listcp", "checkpoints")

    stringArgument("map") {
        playerExecutor { player, args ->
            val map = args["map"] as String
            val cps = TrackEditor.getCheckpoints(map)
            if (cps.isEmpty()) {
                player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.WARNING}Keine Checkpoints für §e$map §7gesetzt.")
                return@playerExecutor
            }
            player.sendMessage("${ChatConfig.INFO_PREFIX}${ChatConfig.INFO}§eCheckpoints für §b$map§e (${cps.size}):")
            cps.forEachIndexed { i, loc ->
                val world = loc.world?.name ?: "?"
                player.sendMessage("  §7#${i + 1} §8→ §f$world §7X§f${loc.blockX} §7Y§f${loc.blockY} §7Z§f${loc.blockZ}")
            }
        }
    }
}


