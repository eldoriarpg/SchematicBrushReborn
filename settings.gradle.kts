rootProject.name = "schematic-brush-reborn"
include(":schematicbrushreborn-api")
include(":schematicbrushreborn-core")
include("schematicbrushreborn-paper")
include("schematicbrushreborn-paper-legacy")
include("schematicbrushreborn-spigot")

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven {
            name = "EldoNexus"
            url = uri("https://eldonexus.de/repository/maven-public/")

        }
    }
}


dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("jackson-databind","com.fasterxml.jackson.core:jackson-databind:2.14.2")
            library("jackson-yaml","com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.2")
            library("adventure-bukkit","net.kyori:adventure-platform-bukkit:4.3.0")
            library("adventure-minimessage", "net.kyori:adventure-text-minimessage:4.13.1")
            library("eldoutil-legacy", "de.eldoria:eldo-util:1.14.4")
            library("eldoutil-jackson", "de.eldoria.util:jackson-configuration:2.0.0-DEV")
            library("messageblocker", "de.eldoria:messageblocker:1.1.1")
            library("jetbrains-annotations", "org.jetbrains:annotations:24.0.1")
        }
    }
}
