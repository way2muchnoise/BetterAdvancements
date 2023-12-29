import net.neoforged.gradle.dsl.common.runs.run.Run
import net.darkhax.curseforgegradle.TaskPublishCurseForge
import net.darkhax.curseforgegradle.Constants as CFG_Constants

plugins {
    id("java")
    id("idea")
    id("maven-publish")
    id("net.neoforged.gradle.userdev")
    // id("org.parchmentmc.librarian.forgegradle") version("1.+")
    id("net.darkhax.curseforgegradle") version("1.1.18")
    id("com.modrinth.minotaur") version("2.+")
}

// gradle.properties
val curseHomepageLink: String by extra
val curseProjectId: String by extra
val modrinthProjectId: String by extra
val neoforgeVersion: String by extra
val mappingsChannel: String by extra
val mappingsParchmentMinecraftVersion: String by extra
val mappingsParchmentVersion: String by extra
val minecraftVersion: String by extra
val modId: String by extra
val modFileName: String by extra
val modJavaVersion: String by extra

val baseArchivesName = "${modFileName}-NeoForge-${minecraftVersion}"
base {
    archivesName.set(baseArchivesName)
}

sourceSets {
}

val dependencyProjects: List<Project> = listOf(
        project(":Common"),
        project(":CommonApi"),
        project(":NeoForgeApi"),
)

dependencyProjects.forEach {
    project.evaluationDependsOn(it.path)
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
    // mappings("official", minecraftVersion)

    accessTransformers {
        file("src/main/resources/META-INF/accesstransformer.cfg")
    }
}

fun commonRunProperties(run: Run) {
    run.modSources(sourceSets.main.get())
    for (p in dependencyProjects) {
        run.modSources(p.sourceSets.main.get())
    }
}

runs {
    create("client") {
        systemProperty("forge.logging.console.level", "debug")
        workingDirectory(file("run/client/Dev"))
        commonRunProperties(this)
    }
    create("client_01") {
        configure("client")
        workingDirectory(file("run/client/Player01"))
        programArguments("--username", "Player01")
        commonRunProperties(this)
    }
    create("client_02") {
        configure("client")
        workingDirectory(file("run/client/Player02"))
        programArguments("--username", "Player02")
        commonRunProperties(this)
    }
    create("server") {
        systemProperty("forge.logging.console.level", "debug")
        workingDirectory(file("run/server"))
        commonRunProperties(this)
    }
}

tasks.named<Jar>("jar") {
    from(sourceSets.main.get().output)
    for (p in dependencyProjects) {
        from(p.sourceSets.main.get().output)
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    // finalizedBy("reobfJar")
}

val apiJar = tasks.register<Jar>("apiJar") {
    from(project(":CommonApi").sourceSets.main.get().output)
    from(project(":NeoForgeApi").sourceSets.main.get().output)

    // TODO: when FG bug is fixed, remove allJava from the api jar.
    // https://github.com/MinecraftForge/ForgeGradle/issues/369
    // Gradle should be able to pull them from the -sources jar.
    from(project(":CommonApi").sourceSets.main.get().allJava)
    from(project(":NeoForgeApi").sourceSets.main.get().allJava)

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    // finalizedBy("reobfJar")
    archiveClassifier.set("api")
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allJava)
    for (p in dependencyProjects) {
        from(p.sourceSets.main.get().allJava)
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    // finalizedBy("reobfJar")
    archiveClassifier.set("sources")
}

tasks.register<TaskPublishCurseForge>("publishCurseForge") {

    apiToken = System.getenv("CURSE_KEY") ?: "0"

    val mainFile = upload(curseProjectId, tasks.jar.get())
    mainFile.changelogType = CFG_Constants.CHANGELOG_MARKDOWN
    mainFile.changelog = System.getenv("CHANGELOG") ?: ""
    mainFile.releaseType = CFG_Constants.RELEASE_TYPE_ALPHA
    mainFile.addModLoader("NeoForge")
    mainFile.addJavaVersion("Java $modJavaVersion")
    mainFile.addGameVersion(minecraftVersion)
    mainFile.withAdditionalFile(apiJar.get())
    mainFile.withAdditionalFile(sourcesJar.get())
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN") ?: "0")
    projectId.set(modrinthProjectId)
    versionNumber.set("${project.version}")
    versionName.set("${project.version} for NeoForge $minecraftVersion")
    versionType.set("alpha")
    uploadFile.set(tasks.jar.get())
    gameVersions.add(minecraftVersion)
    // additionalFiles.addAll(arrayOf(apiJar.get(), sourcesJar.get())) // TODO: Figure out how to upload these
}
tasks.modrinth.get().dependsOn(tasks.jar)

artifacts {
    archives(apiJar.get())
    archives(sourcesJar.get())
    archives(tasks.jar.get())
}

tasks.withType<Jar> {
    destinationDirectory.set(file(rootProject.rootDir.path + "/output"))
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            artifactId = baseArchivesName
            artifact(apiJar.get())
            artifact(sourcesJar.get())
            artifact(tasks.jar.get())
        }
    }
    repositories {
        val deployDir = project.findProperty("DEPLOY_DIR")
        if (deployDir != null) {
            maven(deployDir)
        }
    }
}

idea {
    module {
        for (fileName in listOf("run", "out", "logs")) {
            excludeDirs.add(file(fileName))
        }
    }
}