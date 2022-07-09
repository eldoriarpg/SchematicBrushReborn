import java.time.Instant
import java.time.format.DateTimeFormatter

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("de.eldoria.java-conventions")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
}

val shadebase = "de.eldoria.schematicbrush.libs."

dependencies {
    implementation(project(":schematicbrushreborn-api"))

    testImplementation(project(":schematicbrushreborn-api"))
    testImplementation("org.jetbrains", "annotations", "23.0.0")
    testImplementation("org.mockito", "mockito-core", "4.5.1")
    testImplementation("com.fasterxml.jackson.core", "jackson-databind", "2.13.3")
}

publishData {
    useEldoNexusRepos()
}

fun getBuildType(): String {
    return when {
        System.getenv("PATREON")?.equals("true", true) == true ->{
            "PATREON"
        }
        publishData.isPublicBuild() -> {
            "PUBLIC";
        }
        else -> "LOCAL"
    }
}

tasks {
    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("build.data") {
                expand(
                    "type" to getBuildType(),
                    "time" to DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
                    "branch" to publishData.getBranch(),
                    "commit" to publishData.getCommitHash()
                )
            }
        }

        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        relocate("de.eldoria.eldoutilities", shadebase + "eldoutilities")
        relocate("de.eldoria.messageblocker", shadebase + "messageblocker")
        relocate("net.kyori", shadebase + "kyori")
        mergeServiceFiles()
        archiveClassifier.set("")
        archiveVersion.set(rootProject.version as String)
        archiveBaseName.set("SchematicBrushReborn")
    }

    register<Copy>("copyToServer") {
        val path = project.property("targetDir") ?: ""
        if (path.toString().isEmpty()) {
            println("targetDir is not set in gradle properties")
            return@register
        }
        from(shadowJar)
        destinationDir = File(path.toString())
    }

    build {
        dependsOn(shadowJar)
    }
}

bukkit {
    name = "SchematicBrushReborn"
    version = publishData.getVersion(true)
    description = "Use your world edit schematics as a brush!"
    apiVersion = "1.13"
    main = "de.eldoria.schematicbrush.SchematicBrushRebornImpl"
    authors = listOf("RainbowDashLabs", "SirYwell", "LuftigerLuca")
    website = "https://www.spigotmc.org/resources/79441/"
    depend = listOf("WorldEdit")
    softDepend = listOf("ProtocolLib")

    commands {
        register("schematicbrushadmin") {
            description = "Manage schematic brush"
            aliases = listOf("sbra", "schbra")
        }

        register("schematicbrush") {
            description = "Create and use schematic brushes"
            aliases = listOf("sbr", "schbr", "schembrush")
            permission = "schematicbrush.use"
        }

        register("schematicbrushsettings") {
            description = "Manage the personal settings of schematic brush"
            aliases = listOf("sbrs", "schbs")
            permission = "schematicbrush.brush.use"
        }

        register("schematicbrushpreset") {
            description = "List or edit schematic presets"
            aliases = listOf("sbrp", "schbrp")
            permission = "schematicbrush.preset.use"
        }

        register("schematicbrushbrushpreset") {
            description = "List or edit brush presets"
            aliases = listOf("sbrbp", "schbrbp")
            permission = "schematicbrush.brushpreset.use"
        }
    }

    permissions {
        register("schematicbrush.*") {
            description = "Allows full access to all schematic brush commands"
        }
        register("schematicbrush.brush.use") {
            description = "Allows to use the schematic brush"
        }
        register("schematicbrush.brush.preview") {
            description = "Allows to toggle and use preview"
        }
        register("schematicbrush.preset.use") {
            description = "Access to read and use global brush presets and create and use private brush presets"
        }
        register("schematicbrush.preset.global") {
            description = "Allows to create global presets"
        }
        register("schematicbrush.brushpreset.use") {
            description = "Access to read and use global brush presets and create and use private brush presets"
        }
        register("schematicbrush.brushpreset.global") {
            description = "Allows to create global brush presets"
        }
        register("schematicbrush.admin.reload") {
            description = "Allows to reload the plugin. This might break add on integrations."
        }
        register("schematicbrush.admin.reloadcache") {
            description = "Access to schematic cache reloading"
        }
        register("schematicbrush.admin.debug") {
            description = "Access to debug command"
        }
        register("schematicbrush.admin.migrate") {
            description = "Allows to migrate storage types"
        }
    }
}
