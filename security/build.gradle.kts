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
}
