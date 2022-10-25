import de.chojo.PublishData
import org.cadixdev.gradle.licenser.Licenser

plugins {
    java
    id("org.cadixdev.licenser") version "0.6.1"
    id("de.chojo.publishdata") version "1.0.9"
}

group = "de.eldoria"
version = "2.2.6"

subprojects {
    apply {
        plugin<Licenser>()
        plugin<PublishData>()
    }
}

allprojects {
    license {
        header(rootProject.file("HEADER.txt"))
        include("**/*.java")
    }
}
