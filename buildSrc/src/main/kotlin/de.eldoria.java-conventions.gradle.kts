plugins {
    `java-library`
    `maven-publish`
}

group = "de.eldoria"
version = "2.0.0a"

repositories {
    maven("https://eldonexus.de/repository/maven-public")
    maven("https://eldonexus.de/repository/maven-proxies")
}

dependencies {
    compileOnly("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
    implementation("org.jetbrains", "annotations", "20.1.0")
    implementation("de.eldoria", "eldo-util", "1.10.13-DEV")
    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.1.0")

    testImplementation("com.sk89q.worldedit", "worldedit-bukkit", "7.1.0")
    testImplementation(platform("org.junit:junit-bom:5.7.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
    testImplementation("com.github.seeseemelk", "MockBukkit-v1.16", "1.0.0")
}

allprojects {
    java {
        sourceCompatibility = JavaVersion.VERSION_11
        withSourcesJar()
        withJavadocJar()
    }
}

tasks {
    publish {
        dependsOn(build)
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    compileTestJava{
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
