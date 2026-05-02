// gradle.properties
val fabricVersion: String by extra
val fabricLoaderVersion: String by extra

architectury {
	// Set up Architectury for Fabric.
	fabric()
}

dependencies {
	implementation("net.fabricmc:fabric-loader:${fabricLoaderVersion}")
	implementation("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")
	api(project(":CommonApi"))
}