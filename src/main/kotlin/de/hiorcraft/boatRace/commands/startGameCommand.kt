package de.hiorcraft.boatRace.commands

import de.hiorcraft.boatRace.race.RaceManager
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.kotlindsl.commandTree

fun startGameCommand() = commandTree("start") {

    val rounds = IntegerArgument("rounds")
    val track = RaceTrack.default()
    RaceManager.start(track, rounds)
}