import de.chojo.PublishData
import org.cadixdev.gradle.licenser.Licenser

plugins {
    java
    id("org.cadixdev.licenser") version "0.6.1"
    id("de.chojo.publishdata") version "1.0.9"
}

group = "de.eldoria"
version = "2.4.2"

subprojects {
    apply {
        plugin<Licenser>()
        plugin<JavaPlugin>()
        plugin<PublishData>()
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://eldonexus.de/repository/maven-public/")
        maven("https://eldonexus.de/repository/maven-proxies/")
    }

    license {
        header(rootProject.file("HEADER.txt"))
        include("**/*.java")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        withSourcesJar()
        withJavadocJar()
    }

    dependencies{
        compileOnly("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
        compileOnly("org.jetbrains", "annotations", "24.0.0")
        // Due to incompatibility by the yaml versions defined by world edit, fawe and bukkit we need to exclude it everywhere and add our own version...
        compileOnly("org.yaml", "snakeyaml", "1.33")
        compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.2.13") {
            exclude("org.yaml")
        }
        compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core:2.5.2") {
            exclude("com.intellectualsites.paster")
            exclude("org.yaml")
        }
        compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.5.2") {
            isTransitive = false
            exclude("org.yaml")
        }

        testImplementation(platform("org.junit:junit-bom:5.9.2"))
        testImplementation("org.junit.jupiter", "junit-jupiter")
        testImplementation("com.github.seeseemelk", "MockBukkit-v1.19", "2.145.0")
        testImplementation("com.sk89q.worldedit", "worldedit-bukkit", "7.2.13") {
            exclude("org.yaml")
        }
        testImplementation("com.fastasyncworldedit:FastAsyncWorldEdit-Core:2.5.2") {
            exclude("com.intellectualsites.paster")
            exclude("org.yaml")
        }
        testImplementation("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.5.2") {
            isTransitive = false
            exclude("org.yaml")
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
