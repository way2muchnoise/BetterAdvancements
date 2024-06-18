// gradle.properties
val neoforgeVersion: String by extra
val minecraftVersion: String by extra

architectury {
    // Set up Architectury for NeoForge.
    forge()
}

repositories {
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    forge("net.neoforged:forge:${minecraftVersion}-${neoforgeVersion}")
    api(project(":CommonApi", configuration = "namedElements"))
}

