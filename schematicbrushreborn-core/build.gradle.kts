plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
    `maven-publish`
}

val shadebase = "de.eldoria.schematicbrush.libs."

dependencies {
    implementation(project(":schematicbrushreborn-api")) {
        exclude("com.fasterxml.*")
        exclude("net.kyori")
        exclude("org.jetbrains")
        exclude("org.intellij")
    }
    compileOnly("org.jetbrains", "annotations", "24.0.1")
    compileOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.2")
    compileOnly("com.fasterxml.jackson.core:jackson-core:2.14.2")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.13.1")

    testImplementation(project(":schematicbrushreborn-api"))
    testImplementation("org.jetbrains", "annotations", "24.0.1")
    testImplementation("org.mockito", "mockito-core", "5.3.0")
    testImplementation("com.fasterxml.jackson.core", "jackson-databind", "2.14.2")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

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

