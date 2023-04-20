plugins {
    java
    `java-library`
}

dependencies {
    api(project(":schematicbrushreborn-api")) {
        exclude("com.fasterxml.jackson.core")
        exclude("com.fasterxml.jackson")
        exclude("com.fasterxml.jackson.dataformat")
        exclude("net.kyori")
        exclude("org.jetbrains")
        exclude("org.intellij")
    }
    compileOnly("org.jetbrains", "annotations", "24.0.1")
    compileOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.2")
    compileOnly("com.fasterxml.jackson.core:jackson-core:2.14.2")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.14.2")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.13.1")

    testImplementation(project(":schematicbrushreborn-api"))
    testImplementation("org.jetbrains", "annotations", "24.0.1")
    testImplementation("org.mockito", "mockito-core", "5.3.0")
    testImplementation("com.fasterxml.jackson.core", "jackson-databind", "2.14.2")
}

publishData {
    addBuildData()
    useInternalEldoNexusRepos()
    publishComponent("java")
}
publishing {
    publications.create<MavenPublication>("maven") {
        publishData.configurePublication(this)
        pom {
            url.set("https://github.com/eldoriarpg/schematicbrushreborn")
            developers {
                developer {
                    name.set("Florian Fülling")
                    organization.set("EldoriaRPG")
                    organizationUrl.set("https://github.com/eldoriarpg")
                }
            }
            licenses {
                license {
                    name.set("GNU Affero General Public License v3.0")
                    url.set("https://github.com/eldoriarpg/schematicbrushreborn/blob/master/LICENSE.md")
                }
            }
        }
    }

    repositories {
        maven {
            authentication {
                credentials(PasswordCredentials::class) {
                    username = System.getenv("NEXUS_USERNAME")
                    password = System.getenv("NEXUS_PASSWORD")
                }
            }

            setUrl(publishData.getRepository())
            name = "EldoNexus"
        }
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
}

