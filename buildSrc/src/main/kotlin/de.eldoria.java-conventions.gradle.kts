plugins {
    `java-library`
}

group = "de.eldoria"
version = "2.0.1"

repositories {
    maven("https://eldonexus.de/repository/maven-public")
    maven("https://eldonexus.de/repository/maven-proxies")
}

dependencies {
    compileOnly("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains", "annotations", "20.1.0")
    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.2.8")

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
    compileJava {
        options.encoding = "UTF-8"
    }

    compileTestJava {
        options.encoding = "UTF-8"
    }
}
