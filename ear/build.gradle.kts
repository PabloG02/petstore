// EAR module - Enterprise Application Archive packaging
// TODO: Find a less hacky way to include the WARs in the EAR

plugins {
    ear
}

dependencies {
    // Keep domain and service on the classpath inside /lib
    earlib(project(":service"))
    earlib(project(":domain"))
    earlib(project(":security"))

    // Bundle MySQL driver so WildFly can register the datasource
    earlib(libs.mysql.connector)
}

tasks.named<Ear>("ear") {
    // Ensure WARs are built first
    dependsOn(":jsf:war", ":rest:war")

    // Place WARs at the EAR root
    from(project(":jsf").tasks.named("war"))
    from(project(":rest").tasks.named("war"))
}
