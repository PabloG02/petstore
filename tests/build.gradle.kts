dependencies {
    implementation(project(":domain"))
    implementation(project(":service"))
    implementation(project(":rest"))
    implementation(project(":jsf"))

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}
