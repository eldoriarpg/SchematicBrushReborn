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
            version("jackson", "2.17.2")
            library("jackson-databind", "com.fasterxml.jackson.core","jackson-databind").versionRef("jackson")
            library("jackson-annotations", "com.fasterxml.jackson.core","jackson-annotations").versionRef("jackson")
            library("jackson-yaml", "com.fasterxml.jackson.dataformat","jackson-dataformat-yaml").versionRef("jackson")
            library("snakeyaml", "org.yaml:snakeyaml:2.2")
            bundle("jackson", listOf("jackson-databind", "jackson-annotations", "jackson-yaml"))

            // adventure
            library("adventure-bukkit", "net.kyori:adventure-platform-bukkit:4.3.3")
            library("adventure-minimessage", "net.kyori:adventure-text-minimessage:4.17.0")
            // utilities
            version("utilities", "2.1.1")
            library("eldoutil-plugin", "de.eldoria.util","plugin").versionRef("utilities")
            library("eldoutil-jackson", "de.eldoria.util","jackson-configuration").versionRef("utilities")
            library("eldoutil-serialization", "de.eldoria.util","legacy-serialization").versionRef("utilities")
            library("eldoutil-metrics", "de.eldoria.util","metrics").versionRef("utilities")
            library("eldoutil-updater", "de.eldoria.util","updater").versionRef("utilities")
            library("eldoutil-crossversion", "de.eldoria.util","crossversion").versionRef("utilities")
            bundle("utilities", listOf("eldoutil-jackson", "eldoutil-plugin", "eldoutil-serialization", "eldoutil-metrics",
                "eldoutil-updater", "eldoutil-crossversion"))

            library("bstats", "org.bstats:bstats-bukkit:3.0.2")

            library("messageblocker", "de.eldoria:messageblocker:1.1.3")
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
            library("worldedit", "com.sk89q.worldedit:worldedit-bukkit:7.3.5")
            version("fawe", "2.11.0")
            library("fawe-core", "com.fastasyncworldedit","FastAsyncWorldEdit-Core").versionRef("fawe")
            library("fawe-bukkit", "com.fastasyncworldedit","FastAsyncWorldEdit-Bukkit").versionRef("fawe")

            // plugins
            plugin("publishdata", "de.chojo.publishdata").version("1.4.0")
            plugin("spotless", "com.diffplug.spotless").version("6.25.0")
            plugin("shadow", "io.github.goooler.shadow").version("8.1.8")
            plugin("pluginyml-bukkit", "net.minecrell.plugin-yml.bukkit").version("0.6.0")
            //plugin("pluginyml-paper","net.minecrell.plugin-yml.paper").version( "0.5.3")

        }
        create("testlibs") {
            library("mockbukkit", "com.github.seeseemelk:MockBukkit-v1.19:3.1.0")
        }
    }
}
