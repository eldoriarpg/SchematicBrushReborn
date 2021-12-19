rootProject.name = "SchematicBrushReborn"
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
