plugins {
    `java-library`
    `maven-publish`
}

group = "de.eldoria"
version = "2.0.0d"

repositories {
    maven("https://eldonexus.de/repository/maven-public")
    maven("https://eldonexus.de/repository/maven-proxies")
}

dependencies {
    api("de.eldoria", "eldo-util", "1.10.15-DEV")
    compileOnly("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
    implementation("org.jetbrains", "annotations", "20.1.0")
    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.3.0-SNAPSHOT")
    //compileOnly("com.fastasyncworldedit", "FastAsyncWorldEdit-Bukkit", "1.17-387") { isTransitive = false }
    //compileOnly("com.fastasyncworldedit", "FastAsyncWorldEdit-Core", "1.17-387")

    testImplementation(platform("org.junit:junit-bom:5.7.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
    testImplementation("com.github.seeseemelk", "MockBukkit-v1.16", "1.0.0")
}

allprojects {
    java {
        sourceCompatibility = JavaVersion.VERSION_16
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
