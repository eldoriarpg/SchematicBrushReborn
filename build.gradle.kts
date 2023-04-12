import com.diffplug.gradle.spotless.SpotlessPlugin
import de.chojo.PublishData
import net.minecrell.pluginyml.bukkit.BukkitPlugin

plugins {
    java
    id("com.diffplug.spotless") version "6.18.0"
    id("de.chojo.publishdata") version "1.2.4"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
    `maven-publish`
}

group = "de.eldoria"
version = "2.5.0"

subprojects {
    apply {
        plugin<SpotlessPlugin>()
        plugin<JavaPlugin>()
        plugin<PublishData>()
        plugin<BukkitPlugin>()
        plugin<MavenPublishPlugin>()
    }
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://eldonexus.de/repository/maven-public/")
        maven("https://eldonexus.de/repository/maven-proxies/")
    }

    spotless {
        java {
            licenseHeaderFile(rootProject.file("HEADER.txt"))
            target("**/*.java")
        }
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        withSourcesJar()
        withJavadocJar()
    }

    dependencies {
        compileOnly("io.papermc.paper", "paper-api", "1.17.1-R0.1-SNAPSHOT")
        compileOnly("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
        compileOnly("org.jetbrains", "annotations", "24.0.1")
        // Due to incompatibility by the yaml versions defined by world edit, fawe and bukkit we need to exclude it everywhere and add our own version...
        compileOnly("org.yaml", "snakeyaml", "1.33")
        compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.2.14") {
            exclude("org.yaml")
        }
        compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core:2.6.0") {
            exclude("com.intellectualsites.paster")
            exclude("org.yaml")
            exclude("net.kyori")
        }
        compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.6.0") {
            isTransitive = false
            exclude("org.yaml")
        }

        testImplementation(platform("org.junit:junit-bom:5.9.2"))
        testImplementation("org.junit.jupiter", "junit-jupiter")
        testImplementation("com.github.seeseemelk", "MockBukkit-v1.19", "2.145.0")
        testImplementation("com.sk89q.worldedit", "worldedit-bukkit", "7.2.14") {
            exclude("org.yaml")
        }
        testImplementation("com.fastasyncworldedit:FastAsyncWorldEdit-Core:2.6.0") {
            exclude("com.intellectualsites.paster")
            exclude("org.yaml")
        }
        testImplementation("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.6.0") {
            isTransitive = false
            exclude("org.yaml")
        }
    }

    if (project.name.contains("paper") || project.name.contains("spigot")) {
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
    }

    tasks {
        compileJava {
            options.encoding = "UTF-8"
        }

        compileTestJava {
            options.encoding = "UTF-8"
        }


        test {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
            }
        }
    }
}
