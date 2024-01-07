// gradle.properties
val platforms: String by extra

architectury {
    common(platforms.split(","))
}

loom {
    accessWidenerPath.set(file("src/main/resources/betteradvancements.accesswidener"))
}

dependencies {
    api(project(":CommonApi", configuration = "namedElements"))
}