rootProject.name = "schematic-brush-reborn"
include(":schematicbrushreborn-api")
include(":schematicbrushreborn-core")
include("schematicbrushreborn-paper")

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
            version("jackson", "2.16.2") // This is the internal paper jackson version. do not bump unless the paper version gets bumped
            library("jackson-core", "com.fasterxml.jackson.core","jackson-core").versionRef("jackson")
            library("jackson-databind", "com.fasterxml.jackson.core","jackson-databind").versionRef("jackson")
            library("jackson-annotations", "com.fasterxml.jackson.core","jackson-annotations").versionRef("jackson")
            library("jackson-yaml", "com.fasterxml.jackson.dataformat","jackson-dataformat-yaml").versionRef("jackson")
            bundle("jackson", listOf("jackson-core","jackson-databind", "jackson-annotations", "jackson-yaml"))

            // utilities
            version("utilities", "2.1.11")
            library("eldoutil-plugin", "de.eldoria.util","plugin").versionRef("utilities")
            library("eldoutil-jackson", "de.eldoria.util","jackson-configuration").versionRef("utilities")
            library("eldoutil-serialization", "de.eldoria.util","legacy-serialization").versionRef("utilities")
            library("eldoutil-metrics", "de.eldoria.util","metrics").versionRef("utilities")
            library("eldoutil-updater", "de.eldoria.util","updater").versionRef("utilities")
            library("eldoutil-crossversion", "de.eldoria.util","crossversion").versionRef("utilities")
            bundle("utilities", listOf("eldoutil-jackson", "eldoutil-plugin", "eldoutil-serialization", "eldoutil-metrics",
                "eldoutil-updater", "eldoutil-crossversion"))

            library("bstats", "org.bstats:bstats-bukkit:3.1.0")

            library("messageblocker", "de.eldoria:messageblocker:1.1.3")
            // misc
            library("jetbrains-annotations", "org.jetbrains:annotations:26.0.2-1")
            // minecraft
            version("minecraft-latest", "1.20.1-R0.1-SNAPSHOT")
            library("paper-latest", "io.papermc.paper", "paper-api").version("minecraft-latest")
            library("spigot-latest", "io.papermc.paper", "paper-api").version("minecraft-latest")
            bundle("minecraft-latest", listOf("paper-latest", "spigot-latest"))
            library("paper-v17", "io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
            library("spigot-v16", "io.papermc.paper:paper-api:1.17.1-R0.1-SNAPSHOT")
            // world edit
            library("worldedit", "com.sk89q.worldedit:worldedit-bukkit:7.3.18")
            version("fawe", "2.14.3")
            library("fawe-core", "com.fastasyncworldedit","FastAsyncWorldEdit-Core").versionRef("fawe")
            library("fawe-bukkit", "com.fastasyncworldedit","FastAsyncWorldEdit-Bukkit").versionRef("fawe")

            // plugins
            plugin("publishdata", "de.chojo.publishdata").version("1.4.0")
            plugin("spotless", "com.diffplug.spotless").version("8.1.0")
            plugin("shadow", "com.gradleup.shadow").version("9.3.1")
            plugin("pluginyml-bukkit", "de.eldoria.plugin-yml.bukkit").version("0.8.0")
            plugin("runserver", "xyz.jpenilla.run-paper").version("3.0.2")

        }
        create("testlibs") {
            library("mockbukkit", "com.github.seeseemelk:MockBukkit-v1.19:3.1.0")
        }
    }
}
