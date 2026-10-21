plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    maven("https://eldonexus.de/repository/maven-public/")
}

dependencies {
    implementation(libs.gradle.spotless)
    implementation(libs.gradle.publishdata)
}
