import com.diffplug.gradle.spotless.SpotlessPlugin
import de.chojo.PublishData

plugins {
    java
    alias(libs.plugins.spotless)
    alias(libs.plugins.publishdata)
    `maven-publish`
}

group = "de.eldoria"
version = "2.5.5"

var publishModules = setOf("schematicbrushreborn-api",
        "schematicbrushreborn-core",
        "schematicbrushreborn-paper",
        "schematicbrushreborn-paper-legacy",
        "schematicbrushreborn-spigot")

allprojects {
    repositories {
//        mavenLocal {
//            content {
//                includeGroup("de.eldoria.util")
//            }
//        }
        mavenCentral()
        maven("https://eldonexus.de/repository/maven-public/")
        maven("https://eldonexus.de/repository/maven-proxies/")
    }

    apply {
        plugin<SpotlessPlugin>()
        plugin<JavaPlugin>()
        plugin<PublishData>()
        if (publishModules.contains(project.name)) {
            plugin<MavenPublishPlugin>()
        }
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
        // This is required. https://github.com/gradle/gradle/issues/16634#issuecomment-809345790
        val libs = rootProject.libs
        val testlibs = rootProject.testlibs
        compileOnly(libs.paper.v17)
        compileOnly(libs.spigot.v16)
        compileOnly(libs.jetbrains.annotations)
        // Due to incompatibility by the yaml versions defined by world edit, fawe and bukkit we need to exclude it everywhere and add our own version...
        compileOnly(libs.snakeyaml)
        compileOnly(libs.worldedit)
        compileOnly(libs.fawe.core) {
            exclude("com.intellectualsites.paster")
            exclude("net.kyori")
        }
        compileOnly(libs.fawe.bukkit)

        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter", "junit-jupiter")
        testImplementation(testlibs.mockbukkit)
        testImplementation(libs.worldedit) {
            exclude("org.yaml")
        }
        testImplementation(libs.fawe.core) {
            exclude("com.intellectualsites.paster")
        }
        testImplementation(libs.fawe.bukkit)
    }

    tasks {
        compileJava {
            options.encoding = "UTF-8"
        }

        compileTestJava {
            options.encoding = "UTF-8"
        }

        test {
            dependsOn(spotlessCheck)
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
            }
        }
    }
}
