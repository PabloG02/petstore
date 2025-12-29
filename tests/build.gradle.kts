dependencies {
    implementation(project(":domain"))

    compileOnly(platform(libs.jakarta.bom))
    compileOnly(libs.jakarta.ejb.api)
    compileOnly(libs.jakarta.inject.api)
    compileOnly(libs.jakarta.annotation.api)
    compileOnly(libs.jakarta.cdi.api)
    compileOnly(libs.jakarta.ws.rs.api)

    implementation(libs.hamcrest)
    implementation(libs.dbunit)
}
