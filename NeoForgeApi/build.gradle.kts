plugins {
    id("java")
    id("net.neoforged.gradle.userdev")
    // id("org.parchmentmc.librarian.forgegradle") version ("1.+")
}

// gradle.properties
val neoforgeVersion: String by extra
val mappingsChannel: String by extra
val mappingsParchmentMinecraftVersion: String by extra
val mappingsParchmentVersion: String by extra
val minecraftVersion: String by extra
val modJavaVersion: String by extra

val dependencyProjects: List<Project> = listOf(
        project(":CommonApi")
)

dependencyProjects.forEach {
    project.evaluationDependsOn(it.path)
}

sourceSets {
    named("main") {
        resources {
            //The API has no resources
            setSrcDirs(emptyList<String>())
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(modJavaVersion))
    }
}

dependencies {
    implementation(
            group = "net.neoforged",
            name = "neoforge",
            version = "${neoforgeVersion}"
    )
    dependencyProjects.forEach {
        implementation(it)
    }
}

minecraft {
    // mappings(mappingsChannel, "${mappingsParchmentMinecraftVersion}-${mappingsParchmentVersion}-${minecraftVersion}")
    //mappings("official", minecraftVersion)

    // All minecraft configurations in the multi-project must be identical, including ATs,
    // because of a ForgeGradle bug https://github.com/MinecraftForge/ForgeGradle/issues/844
    accessTransformers {
        file("../NeoForge/src/main/resources/META-INF/accesstransformer.cfg")
    }

    // no runs are configured for API
}
