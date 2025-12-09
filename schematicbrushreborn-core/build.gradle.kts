plugins {
    java
    `java-library`
}

dependencies {
    api(project(":schematicbrushreborn-api"))
    compileOnly(libs.jetbrains.annotations)
    compileOnly("org.apache.logging.log4j", "log4j-slf4j2-impl", "2.25.2")
    compileOnly("org.apache.logging.log4j", "log4j-core", "2.25.2")

    implementation(libs.bstats)

    testImplementation(project(":schematicbrushreborn-api"))
    testImplementation(libs.jetbrains.annotations)
    testImplementation("org.mockito", "mockito-core", "5.20.0")
    testImplementation(libs.jackson.databind)
    testImplementation(libs.bundles.utilities)
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
                    name.set("Nora Fülling")
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

