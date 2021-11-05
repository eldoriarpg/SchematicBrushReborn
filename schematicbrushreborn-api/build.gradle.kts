plugins {
    java
    id("de.eldoria.library-conventions")
}

group = "de.eldoria"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.kyori", "adventure-api", "4.9.2")
    api("de.eldoria", "eldo-util", "1.10.15-DEV")

    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.6.0")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine")
}
