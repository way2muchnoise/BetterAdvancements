plugins {
    java
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
}

// gradle.properties
val minecraftVersion: String by extra

val dependencyProjects: List<Project> = listOf(
    project(":CommonApi"),
)

dependencyProjects.forEach {
    project.evaluationDependsOn(it.path)
}

minecraft {
    version(minecraftVersion)
    // no runs are configured for Common
    accessWideners(file("src/main/resources/betteradvancements.accesswidener"))
}

sourceSets {

}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(
        group = "org.spongepowered",
        name = "mixin",
        version = "0.8.5"
    )
    dependencyProjects.forEach {
        implementation(it)
    }
}