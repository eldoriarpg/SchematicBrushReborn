import java.util.zip.ZipFile

plugins {
    id("sbr.publishing-conventions")
}

val eldoUtilJavadoc: Configuration by configurations.creating {
    isTransitive = false
}

dependencies {
    compileOnlyApi(libs.bundles.utilities)
    compileOnlyApi(platform(libs.jackson.bom))
    compileOnlyApi(libs.bundles.jackson)

    for (module in libs.bundles.utilities.get()) {
        eldoUtilJavadoc("${module.module}:${module.version}:javadoc")
    }
}

publishData {
    eldoNexus(dev = false)
}

val eldoUtilPackages = layout.buildDirectory.dir("eldo-util-javadoc")

val eldoUtilElementList by tasks.registering {
    val jars: FileCollection = eldoUtilJavadoc
    val target = eldoUtilPackages.map { it.file("element-list") }
    inputs.files(jars)
    outputs.file(target)
    doLast {
        val packages = sortedSetOf<String>()
        jars.forEach { jar ->
            ZipFile(jar).use { zip ->
                zip.getEntry("element-list")?.let { entry ->
                    zip.getInputStream(entry).bufferedReader().readLines().filter { it.isNotBlank() }.forEach(packages::add)
                }
            }
        }
        target.get().asFile.writeText(packages.joinToString("\n", postfix = "\n"))
    }
}

tasks {
    withType<Javadoc> {
        dependsOn(eldoUtilElementList)
        val options = options as StandardJavadocDocletOptions
        options.links("https://jd.papermc.io/paper/1.21.11/")
        options.linksOffline(
                "https://docs.eldoria.de/eldo-util/javadoc/",
                eldoUtilPackages.get().asFile.path
        )
    }
}
