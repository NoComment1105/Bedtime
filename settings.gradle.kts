pluginManagement {
    plugins {
        // Update this in libs.version.toml when you change it here
        kotlin("jvm") version "1.7.10"
        kotlin("plugin.serialization") version "1.7.10"
        // Update this in libs.version.toml when you change it here
        id("io.gitlab.arturbosch.detekt") version "1.21.0"
        id("com.github.jakemarsden.git-hooks") version "0.0.2"
        id("com.github.johnrengelman.shadow") version "7.1.2"
        id("org.cadixdev.licenser") version "0.6.1"
    }
}

rootProject.name = "BedTime"

enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}
