rootProject.name = "schematic-brush-reborn"
include(":schematicbrushreborn-api")
include(":schematicbrushreborn-core")

pluginManagement{
    repositories{
        mavenLocal()
        gradlePluginPortal()
        maven{
            name = "EldoNexus"
            url = uri("https://eldonexus.de/repository/maven-public/")

        }
    }
}
include("schematicbrushreborn-paper")
include("schematicbrushreborn-paper-legacy")
include("schematicbrushreborn-spigot")
