plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
    `maven-publish`
}

val shadebase = "de.eldoria.schematicbrush.libs."


dependencies {
    implementation(project(":schematicbrushreborn-core"))
}

publishData {
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
        if (publishData.isPublicBuild()) {
            relocate("de.eldoria.eldoutilities", shadebase + "eldoutilities")
            relocate("de.eldoria.messageblocker", shadebase + "messageblocker")
            relocate("net.kyori", shadebase + "kyori")
        }
        mergeServiceFiles()
        archiveClassifier.set("")
        archiveVersion.set(rootProject.version as String)
    }

    build {
        dependsOn(shadowJar)
    }
}
