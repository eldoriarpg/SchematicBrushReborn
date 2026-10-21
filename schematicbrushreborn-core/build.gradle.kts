plugins {
    id("sbr.publishing-conventions")
}

dependencies {
    api(project(":schematicbrushreborn-api"))
    compileOnly(libs.jetbrains.annotations)
    compileOnly(libs.log4j.slf4j2)
    compileOnly(libs.log4j.core)

    implementation(libs.bstats)

    testImplementation(project(":schematicbrushreborn-api"))
    testImplementation(libs.jetbrains.annotations)
    testImplementation(libs.mockito)
    testImplementation(libs.jackson.databind)
    testImplementation(libs.bundles.utilities)
}

publishData {
    eldoNexus(internal = true, dev = false)
}
