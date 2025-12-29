dependencies {
    compileOnly(platform(libs.jakarta.bom))
    compileOnly(libs.jakarta.persistence.api)
    compileOnly(libs.jakarta.jsonb.api)

    implementation(libs.commons.lang3)

    // JUnit 5
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    // Hamcrest
    testImplementation(libs.hamcrest)
}

tasks.test {
    useJUnitPlatform()
}
