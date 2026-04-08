plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin") version "1.21.11+"
}

version = findProperty("version") as String
group = "de.hiorcraft.boatRace"

surfPaperPluginApi {
    mainClass("de.hiorcraft.boatRace.PaperMain")
    generateLibraryLoader(false)

    authors.add("Hiorcraft")
}