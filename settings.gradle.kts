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
            // jackson & serialization
            library("jackson-databind","com.fasterxml.jackson.core:jackson-databind:2.14.2")
            library("jackson-yaml","com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.2")
            library("snakeyaml", "org.yaml:snakeyaml:1.33")
            // adventure
            library("adventure-bukkit","net.kyori:adventure-platform-bukkit:4.3.0")
            library("adventure-minimessage", "net.kyori:adventure-text-minimessage:4.13.1")
            // utilities
            library("eldoutil-legacy", "de.eldoria:eldo-util:1.14.4")
            library("eldoutil-jackson", "de.eldoria.util:jackson-configuration:2.0.0-DEV")
            library("messageblocker", "de.eldoria:messageblocker:1.1.1")
            // misc
            library("jetbrains-annotations", "org.jetbrains:annotations:24.0.1")
            // minecraft
            library("paper-latest", "io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
            library("paper-v17", "io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
            library("spigot-v16", "io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
            // world edit
            library("worldedit", "com.sk89q.worldedit:worldedit-bukkit:7.2.14")
            library("fawe-core", "com.fastasyncworldedit:FastAsyncWorldEdit-Core:2.6.1")
            library("fawe-bukkit", "com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.6.1")
            // testing
            library("mockbukkit", "com.github.seeseemelk:MockBukkit-v1.19:2.147.1")
        }
    }
}
