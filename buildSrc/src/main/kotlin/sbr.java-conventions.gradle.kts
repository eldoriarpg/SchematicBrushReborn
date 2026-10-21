plugins {
    `java-library`
    id("com.diffplug.spotless")
}

val libs = versionCatalogs.named("libs")

repositories {
    mavenCentral()
    maven("https://eldonexus.de/repository/maven-public/")
    maven("https://eldonexus.de/repository/maven-proxies/")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
    withJavadocJar()
}

spotless {
    java {
        licenseHeaderFile(rootProject.file("HEADER.txt"))
        target("**/*.java")
    }
}

dependencies {
    compileOnly(libs.findLibrary("paper-v17").get())
    compileOnly(libs.findLibrary("jetbrains-annotations").get())
    compileOnly(libs.findLibrary("worldedit").get())
    compileOnly(libs.findLibrary("fawe-core").get()) {
        exclude("com.intellectualsites.paster")
        exclude("net.kyori")
    }
    compileOnly(libs.findLibrary("fawe-bukkit").get())

    testImplementation(libs.findLibrary("junit-jupiter-api").get())
    testRuntimeOnly(libs.findLibrary("junit-platform-launcher").get())
    testRuntimeOnly(libs.findLibrary("junit-jupiter-engine").get())
    testImplementation(libs.findLibrary("mockbukkit").get())
    testImplementation(libs.findLibrary("worldedit").get()) {
        exclude("org.yaml")
    }
    testImplementation(libs.findLibrary("fawe-core").get()) {
        exclude("com.intellectualsites.paster")
    }
    testImplementation(libs.findLibrary("fawe-bukkit").get())
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    compileTestJava {
        options.encoding = "UTF-8"
    }

    test {
        dependsOn(spotlessCheck)
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
