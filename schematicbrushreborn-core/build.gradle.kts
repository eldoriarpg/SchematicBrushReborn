plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("de.eldoria.java-conventions")
}

val shadebase = "de.eldoria.schematicbrush.libs."

dependencies {
    implementation(project(":schematicbrushreborn-api"))

    implementation("de.eldoria", "messageblocker", "1.0.3c-DEV")
    implementation("net.kyori", "adventure-platform-bukkit", "4.1.0")
    implementation("net.kyori", "adventure-text-minimessage", "4.10.1")
    testImplementation("org.jetbrains", "annotations", "21.0.1")
    testImplementation("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.7.1")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.7.1")
    testImplementation("org.mockito", "mockito-core", "3.5.13")
}

publishData {
    hashLength = 7
    useEldoNexusRepos()
    publishTask("jar")
    publishTask("shadowJar")
    publishTask("sourcesJar")
    publishTask("javadocJar")
}


tasks {
    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("plugin.yml") {
                expand(
                    "name" to project.rootProject.name,
                    "version" to publishData.getVersion(true)
                )
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        relocate("de.eldoria.eldoutilities", shadebase + "eldoutilities")
        relocate("de.eldoria.messageblocker", shadebase + "messageblocker")
        relocate("net.kyori", shadebase + "kyori")
        mergeServiceFiles()
        minimize()
        archiveClassifier.set("")
        archiveBaseName.set("SchematicBrushReborn")
    }

    register<Copy>("copyToServer") {
        val path = project.property("targetDir") ?: ""
        if (path.toString().isEmpty()) {
            println("targetDir is not set in gradle properties")
            return@register
        }
        from(shadowJar)
        destinationDir = File(path.toString())
    }

    build{
        dependsOn(shadowJar)
    }
}
