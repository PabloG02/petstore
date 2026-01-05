plugins {
    `java-library`
}

dependencies {
    compileOnly(platform(libs.jakarta.bom))
    compileOnly(libs.jakarta.security.enterprise.api)
    compileOnly(libs.jakarta.inject.api)
    compileOnly(libs.jakarta.cdi.api)
    compileOnly(libs.jakarta.annotation.api)
    compileOnly(libs.jakarta.servlet.api)

    // Jakarta APIs for test compilation
    testImplementation(platform(libs.jakarta.bom))
    testImplementation(libs.jakarta.security.enterprise.api)

    // JUnit 5
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}
