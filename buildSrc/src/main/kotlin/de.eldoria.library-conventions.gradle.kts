plugins {
    `java-library`
    `maven-publish`
    id("de.eldoria.java-conventions")
}

publishing {
    val publishData = PublishData(project)
    publications {

        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = project.group as String?
            artifactId = project.name.toLowerCase()
            version = publishData.getVersion()
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
    }

    repositories {
        maven {
            name = "EldoNexus"
            url = uri(publishData.getRepository())

            authentication {
                credentials(PasswordCredentials::class) {
                    username = System.getenv("NEXUS_USERNAME")
                    password = System.getenv("NEXUS_PASSWORD")
                }
            }
        }
    }
}
