import org.cadixdev.gradle.licenser.Licenser

plugins {
    java
    id("org.cadixdev.licenser") version "0.6.1"
}

subprojects {
    apply {
        plugin<Licenser>()
    }
}

allprojects {
    license {
        header(rootProject.file("HEADER.txt"))
        include("**/*.java")
    }
}
