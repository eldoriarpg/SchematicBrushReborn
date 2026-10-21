plugins {
    `java-library`
    id("com.diffplug.spotless")
}

val libs = versionCatalogs.named("libs")

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://eldonexus.de/repository/maven-public/")
    maven("https://eldonexus.de/repository/maven-proxies/")
    maven("https://repo.opencollab.dev/main/") {
        content { includeGroupAndSubgroups("org.geysermc") }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
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
    compileOnly(libs.findLibrary("paper").get())
    compileOnly(libs.findLibrary("jetbrains-annotations").get())
    compileOnly(libs.findLibrary("fawe-core").get()) {
        exclude("com.intellectualsites.paster")
        exclude("net.kyori")
    }
    compileOnly(libs.findLibrary("fawe-bukkit").get()) {
        exclude("net.kyori")
    }

    testImplementation(libs.findLibrary("junit-jupiter-api").get())
    testRuntimeOnly(libs.findLibrary("junit-platform-launcher").get())
    testRuntimeOnly(libs.findLibrary("junit-jupiter-engine").get())
    testImplementation(libs.findLibrary("mockbukkit").get())
    testImplementation(libs.findLibrary("paper").get())
    testImplementation(libs.findLibrary("fawe-core").get()) {
        exclude("com.intellectualsites.paster")
        exclude("net.kyori")
    }
    testImplementation(libs.findLibrary("fawe-bukkit").get()) {
        exclude("net.kyori")
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
        dependsOn(spotlessCheck)
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
