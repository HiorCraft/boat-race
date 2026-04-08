package de.hiorcraft.boatRace.race

enum class RaceState {
    WAITING,      // Warten auf Spieler / Queue offen
    COUNTDOWN,    // Countdown läuft
    RUNNING,      // Rennen läuft
    FINISHED      // Rennen beendet
}
