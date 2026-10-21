plugins {
    id("sbr.publishing-conventions")
}

dependencies {
    compileOnlyApi(libs.bundles.utilities)
    compileOnlyApi(libs.bundles.jackson)
    api(libs.messageblocker)
}

publishData {
    eldoNexus(dev = false)
}

tasks {
    withType<Javadoc> {
        val options = options as StandardJavadocDocletOptions
        options.links(
                "https://hub.spigotmc.org/javadocs/spigot/",
                "https://eldoriarpg.github.io/eldo-util/"
        )
    }
}
