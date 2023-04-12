dependencies {
    bukkitLibrary("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.2")
    bukkitLibrary("com.fasterxml.jackson.core:jackson-core:2.14.2")
    bukkitLibrary("com.fasterxml.jackson.core:jackson-databind:2.14.2")

    implementation(project(":schematicbrushreborn-core"))

}

tasks {
    register<Copy>("copyToServer") {
        val path = project.property("targetDir") ?: ""
        if (path.toString().isEmpty()) {
            println("targetDir is not set in gradle properties")
            return@register
        }
        from(compileJava)
        destinationDir = File(path.toString())
    }
}


bukkit {
    name = "SchematicBrushReborn"
    version = publishData.getVersion(true)
    description = "Use your world edit schematics as a brush!"
    apiVersion = "1.16"
    main = "de.eldoria.schematicbrush.SchematicBrushRebornImpl"
    authors = listOf("RainbowDashLabs", "SirYwell", "LuftigerLuca")
    website = "https://www.spigotmc.org/resources/98499/"
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
        register("schematicbrushmodify") {
            description = "Edit settings of the current brush"
            aliases = listOf("sbrm", "schbrm")
            permission = "schematicbrush.brush.use"
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
