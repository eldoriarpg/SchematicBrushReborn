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
    compileOnly(libs.jetbrains.annotations)
    compileOnly(libs.jackson.yaml)
    compileOnly(libs.adventure.bukkit)
    compileOnly(libs.adventure.minimessage)

    testImplementation(project(":schematicbrushreborn-api"))
    testImplementation(libs.jetbrains.annotations)
    testImplementation("org.mockito", "mockito-core", "5.6.0")
    testImplementation(libs.jackson.databind)
}
publishData {
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

