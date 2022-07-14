plugins {
    `java-library`
}

repositories {
    maven("https://eldonexus.de/repository/maven-public")
    maven("https://eldonexus.de/repository/maven-proxies")
}

dependencies {
    compileOnly("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains", "annotations", "23.0.0")
    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.2.10")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core:2.4.0"){
        exclude("com.intellectualsites.paster")
    }
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.4.1") { isTransitive = false }

    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter", "junit-jupiter")
    testImplementation("com.github.seeseemelk", "MockBukkit-v1.19", "2.73.0")
    testImplementation("com.sk89q.worldedit", "worldedit-bukkit", "7.2.10")
    testImplementation("com.fastasyncworldedit:FastAsyncWorldEdit-Core:2.4.0"){
        exclude("com.intellectualsites.paster")
    }
    testImplementation("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.4.1") { isTransitive = false }
}

allprojects {
    java {
        sourceCompatibility = JavaVersion.VERSION_17
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


    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
