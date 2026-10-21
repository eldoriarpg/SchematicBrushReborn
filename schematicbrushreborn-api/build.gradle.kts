plugins {
    id("sbr.publishing-conventions")
}

dependencies {
    compileOnlyApi(libs.bundles.utilities)
    compileOnlyApi(platform(libs.jackson.bom))
    compileOnlyApi(libs.bundles.jackson)
}

publishData {
    eldoNexus(dev = false)
}

tasks {
    withType<Javadoc> {
        val options = options as StandardJavadocDocletOptions
        options.links(
                "https://jd.papermc.io/paper/1.21.11/",
                "https://eldoriarpg.github.io/eldo-util/"
        )
    }
}
