plugins {
    alias(libs.plugins.pluginyml.bukkit)
    alias(libs.plugins.shadow)
}

val shadebase = "de.eldoria.schematicbrush.libs."

dependencies {
    bukkitLibrary(libs.jackson.yaml)
    bukkitLibrary(libs.adventure.bukkit)
    bukkitLibrary(libs.adventure.minimessage)

    implementation(project(":schematicbrushreborn-core"))
}

publishData {
    addBuildData()
    useInternalEldoNexusRepos()
    publishTask("shadowJar")
}
publishing {
    publications.create<MavenPublication>("maven") {
        publishData.configurePublication(this)
        pom {
            url.set("https://github.com/eldoriarpg/schematicbrushreborn")
            developers {
                developer {
                    name.set("Florian Fülling")
                    organization.set("EldoriaRPG")
                    organizationUrl.set("https://github.com/eldoriarpg")
                }
            }
            licenses {
                license {
                    name.set("GNU Affero General Public License v3.0")
                    url.set("https://github.com/eldoriarpg/schematicbrushreborn/blob/master/LICENSE.md")
                }
            }
        }
    }

    repositories {
        maven {
            authentication {
                credentials(PasswordCredentials::class) {
                    username = System.getenv("NEXUS_USERNAME")
                    password = System.getenv("NEXUS_PASSWORD")
                }
            }

            setUrl(publishData.getRepository())
            name = "EldoNexus"
        }
    }
}

tasks {
    shadowJar {
        relocate("de.eldoria.eldoutilities", shadebase + "eldoutilities")
        relocate("de.eldoria.jacksonbukkit", shadebase + "jacksonbukkit")
        relocate("de.eldoria.messageblocker", shadebase + "messageblocker")
        mergeServiceFiles()
        archiveVersion.set(rootProject.version as String)
    }
    build {
        dependsOn(shadowJar)
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
