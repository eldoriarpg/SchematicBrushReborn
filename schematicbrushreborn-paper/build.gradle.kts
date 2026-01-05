plugins {
    alias(libs.plugins.pluginyml.bukkit)
    alias(libs.plugins.shadow)
    alias(libs.plugins.runserver)
}

val shadebase = "de.eldoria.schematicbrush.libs."

dependencies {
    implementation(project(":schematicbrushreborn-core")) {
        exclude("org.yaml", "snakeyaml")
        exclude("com.fasterxml")
    }

    implementation(libs.bundles.utilities) {
        exclude("com.fasterxml")
        exclude("net.kyori")
        exclude("org.jetbrains")
        exclude("org.yaml")
    }

    implementation(libs.bundles.jackson) {
        exclude("org.yaml")
    }
    //bukkitLibrary(libs.adventure.bukkit)
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
                    name.set("Nora Fülling")
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
        relocate("org.bstats", shadebase + "bstats")
        relocate("de.eldoria.messageblocker", shadebase + "messageblocker")
        relocate("com.fasterxml", shadebase + "fasterxml")
        relocate("de.eldoria.jacksonbukkit", shadebase + "jacksonbukkit")
        relocate("de.eldoria.eldoutilities", shadebase + "utilities")
        mergeServiceFiles()
        archiveVersion.set(rootProject.version as String)
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion("1.21.10")
        downloadPlugins {
            url("https://ci.athion.net/job/FastAsyncWorldEdit/1231/artifact/artifacts/FastAsyncWorldEdit-Paper-2.14.3-SNAPSHOT-1231.jar")
            url("https://download.luckperms.net/1611/bukkit/loader/LuckPerms-Bukkit-5.5.22.jar")
            url("https://lyna.eldoria.de/api/v1/download/direct/8/18/latest") // Grid Selector
            url("https://lyna.eldoria.de/api/v1/download/direct/6/2/latest") // Tools
            url("https://lyna.eldoria.de/api/v1/download/direct/9/18/latest") // Database
            url("https://lyna.eldoria.de/api/v1/download/direct/7/1/latest") // Survival

        }

        jvmArgs("-Dcom.mojang.eula.agree=true")
    }
}


bukkit {
    name = "SchematicBrushReborn"
    version = publishData.getVersion(true)
    description = "Use your world edit schematics as a brush!"
    apiVersion = "1.16"
    main = "de.eldoria.schematicbrush.SchematicBrushRebornImpl"
    authors = listOf("RainbowDashLabs", "SirYwell", "LuftigerLuca")
    website = "https://sbr.discord.eldoria.de"
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
            permission = "schematicbrush.brush.use"
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
