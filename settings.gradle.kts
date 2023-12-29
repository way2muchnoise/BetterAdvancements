pluginManagement {
	repositories {
		maven("https://maven.minecraftforge.net/")
		maven("https://maven.parchmentmc.org/")
		maven("https://maven.blamejared.com/")
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/") {
			name = "Fabric"
		}
		maven("https://repo.spongepowered.org/repository/maven-public/") {
			name = "Sponge Snapshots"
		}
		maven("https://maven.neoforged.net/releases")
	}
	plugins {
		id("net.neoforged.gradle.common") version("7.0.72")
		id("net.neoforged.gradle.userdev") version("7.0.72")
	}
	resolutionStrategy {
		eachPlugin {
			if (requested.id.id == "net.minecraftforge.gradle") {
				useModule("${requested.id}:ForgeGradle:${requested.version}")
			}
			if (requested.id.id == "org.spongepowered.mixin") {
				useModule("org.spongepowered:mixingradle:${requested.version}")
			}
		}
	}
}

val minecraftVersion: String by settings

rootProject.name = "BetterAdvancements"
include(
		"CommonApi", "Common",
		"FabricApi", "Fabric",
		"ForgeApi", "Forge",
		"NeoForgeApi", "NeoForge"
)