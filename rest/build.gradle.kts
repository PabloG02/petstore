plugins {
    war
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":service"))

    compileOnly(platform(libs.jakarta.bom))
    compileOnly(libs.jakarta.ws.rs.api)
    compileOnly(libs.jakarta.ejb.api)
    compileOnly(libs.jakarta.persistence.api)
    compileOnly(libs.jakarta.inject.api)
    compileOnly(libs.jakarta.cdi.api)

    // Test support from tests module
    testImplementation(project(":tests"))

    // Jakarta APIs for test compilation
    testImplementation(platform(libs.jakarta.bom))
    testImplementation(libs.jakarta.ws.rs.api)
    testImplementation(libs.jakarta.ejb.api)
    testImplementation(libs.jakarta.persistence.api)
    testImplementation(libs.jakarta.inject.api)

    // JUnit 5
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)

    // Hamcrest
    testImplementation(libs.hamcrest)

    // Mockito
    testImplementation(libs.mockito.junit.jupiter)

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

    // ShrinkWrap Resolver
    testImplementation(platform(libs.shrinkwrap.resolver.bom))
    testImplementation(libs.shrinkwrap.resolver)
    testImplementation(libs.gradle.tooling.api)

    // JAX-RS client implementation for @RunAsClient tests
    testRuntimeOnly(libs.resteasy.client)
    testRuntimeOnly(libs.resteasy.jsonb.provider)
}

tasks.test {
    useJUnitPlatform()
}
