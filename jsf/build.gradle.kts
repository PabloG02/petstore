plugins {
    id("war")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":service"))
}
