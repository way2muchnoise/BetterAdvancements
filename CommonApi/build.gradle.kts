// gradle.properties
val platforms: String by extra

architectury {
    common(platforms.split(","))
}

dependencies {
    // None
}