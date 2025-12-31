plugins {
    war
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":service"))

    compileOnly(platform(libs.jakarta.bom))
    compileOnly(libs.jakarta.faces.api)
    compileOnly(libs.jakarta.servlet.api)
    compileOnly(libs.jakarta.cdi.api)
    compileOnly(libs.jakarta.ejb.api)
    compileOnly(libs.jakarta.inject.api)
    compileOnly(libs.jakarta.annotation.api)
    compileOnly(libs.jakarta.persistence.api)
}
