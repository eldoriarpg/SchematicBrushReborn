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
            version("jackson", "2.15.3")
            library("jackson-databind", "com.fasterxml.jackson.core","jackson-databind").versionRef("jackson")
            library("jackson-annotations", "com.fasterxml.jackson.core","jackson-annotations").versionRef("jackson")
            library("jackson-yaml", "com.fasterxml.jackson.dataformat","jackson-dataformat-yaml").versionRef("jackson")
            library("snakeyaml", "org.yaml:snakeyaml:2.2")
            bundle("jackson", listOf("jackson-databind", "jackson-annotations", "jackson-yaml"))

            // adventure
            library("adventure-bukkit", "net.kyori:adventure-platform-bukkit:4.3.2")
            library("adventure-minimessage", "net.kyori:adventure-text-minimessage:4.15.0")
            // utilities
            library("eldoutil-legacy", "de.eldoria:eldo-util:1.14.4")
            library("eldoutil-jackson", "de.eldoria.util:jackson-configuration:2.0.3")
            library("messageblocker", "de.eldoria:messageblocker:1.1.2")
            // misc
            library("jetbrains-annotations", "org.jetbrains:annotations:24.1.0")
            // minecraft
            version("minecraft-latest", "1.20.1-R0.1-SNAPSHOT")
            library("paper-latest", "io.papermc.paper", "paper-api").version("minecraft-latest")
            library("spigot-latest", "io.papermc.paper", "paper-api").version("minecraft-latest")
            bundle("minecraft-latest", listOf("paper-latest", "spigot-latest"))
            library("paper-v17", "io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
            library("spigot-v16", "io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
            // world edit
            library("worldedit", "com.sk89q.worldedit:worldedit-bukkit:7.3.0")
            version("fawe", "2.9.1")
            library("fawe-core", "com.fastasyncworldedit","FastAsyncWorldEdit-Core").versionRef("fawe")
            library("fawe-bukkit", "com.fastasyncworldedit","FastAsyncWorldEdit-Bukkit").versionRef("fawe")

            // plugins
            plugin("publishdata", "de.chojo.publishdata").version("1.4.0")
            plugin("spotless", "com.diffplug.spotless").version("6.24.0")
            plugin("shadow", "com.github.johnrengelman.shadow").version("8.1.1")
            plugin("pluginyml-bukkit", "net.minecrell.plugin-yml.bukkit").version("0.6.0")
            //plugin("pluginyml-paper","net.minecrell.plugin-yml.paper").version( "0.5.3")

        }
        create("testlibs") {
            library("mockbukkit", "com.github.seeseemelk:MockBukkit-v1.19:3.1.0")
        }
    }
}
