plugins {
    java
    id("de.eldoria.library-conventions")
}

dependencies {
    api("de.eldoria", "eldo-util", "1.11.0-DEV")

    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.6.0")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine")
}
