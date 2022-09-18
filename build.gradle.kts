import de.chojo.PublishData
import org.cadixdev.gradle.licenser.Licenser

plugins {
    java
    id("org.cadixdev.licenser") version "0.6.1"
    id("de.chojo.publishdata") version "1.0.8"
}

group = "de.eldoria"
version = "2.2.5"

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
