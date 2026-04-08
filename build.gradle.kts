plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin") version "1.21.11+"
}

group = "de.hiorcraft.boatRace"
version = findProperty("version") as String

surfPaperPluginApi {
    mainClass("de.hiorcraft.boatRace.PaperMain")
    generateLibraryLoader(false)

    authors.add("Hiorcraft")
}