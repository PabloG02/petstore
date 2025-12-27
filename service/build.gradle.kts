dependencies {
    implementation(project(":domain"))

    testImplementation(project(":tests"))

    compileOnly(platform(libs.jakarta.bom))
    compileOnly(libs.jakarta.persistence.api)
    compileOnly(libs.jakarta.ejb.api)
    compileOnly(libs.jakarta.inject.api)
    compileOnly(libs.jakarta.transaction.api)
    compileOnly(libs.jakarta.annotation.api)

    // Jakarta APIs for test compilation
    testImplementation(platform(libs.jakarta.bom))
    testCompileOnly(libs.jakarta.persistence.api)
    testCompileOnly(libs.jakarta.ejb.api)
    testCompileOnly(libs.jakarta.inject.api)
    testCompileOnly(libs.jakarta.transaction.api)
    testCompileOnly(libs.jakarta.annotation.api)

    // JUnit 5
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)

    // Hamcrest
    testImplementation(libs.hamcrest)

    // Arquillian
    testImplementation(platform(libs.arquillian.bom))
    testImplementation(platform(libs.arquillian.jakarta.bom))
    testImplementation(platform(libs.wildfly.arquillian.bom))
    testImplementation(libs.arquillian.junit5.container)
    testImplementation(libs.wildfly.arquillian.container.managed)
    testImplementation(libs.shrinkwrap.api)
    testImplementation(libs.shrinkwrap.impl)

    // DBUnit
    testImplementation(libs.dbunit)

    // ShrinkWrap Resolver (for including dependencies in deployment)
    testImplementation(platform(libs.shrinkwrap.resolver.bom))
    testImplementation(libs.shrinkwrap.resolver)
    testImplementation(libs.gradle.tooling.api) // Overwrite version to add support for newer JDKs
}

tasks.test {
    useJUnitPlatform()
}
