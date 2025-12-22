dependencies {
    implementation(project(":domain"))
    implementation(project(":service"))
    implementation(project(":rest"))
    implementation(project(":jsf"))
    
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
