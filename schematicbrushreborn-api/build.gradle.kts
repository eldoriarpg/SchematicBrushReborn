plugins {
    java
    id("de.eldoria.library-conventions")
}

dependencies {
    api("de.eldoria", "eldo-util", "1.12.0-DEV")

    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.6.0")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine")
}

tasks {
    test {
        dependsOn(licenseCheck)
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
