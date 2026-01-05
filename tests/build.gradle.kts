dependencies {
    implementation(project(":domain"))
    implementation(project(":security"))

    compileOnly(platform(libs.jakarta.bom))
    compileOnly(libs.jakarta.ejb.api)
    compileOnly(libs.jakarta.inject.api)
    compileOnly(libs.jakarta.annotation.api)
    compileOnly(libs.jakarta.cdi.api)
    compileOnly(libs.jakarta.ws.rs.api)
    compileOnly(libs.jakarta.security.enterprise.api)

    implementation(libs.hamcrest)
    implementation(libs.dbunit)
}
