// EAR module - Enterprise Application Archive packaging
// TODO: Find a less hacky way to include the WARs in the EAR

import org.gradle.api.tasks.bundling.War

plugins {
    ear
}

// Make sure the child projects are evaluated so their WAR tasks exist
evaluationDependsOn(":jsf")
evaluationDependsOn(":rest")

dependencies {
    // Keep domain and service on the classpath inside /lib
    earlib(project(":service"))
    earlib(project(":domain"))
    earlib(project(":security"))

    // Bundle MySQL driver so WildFly can register the datasource
    earlib(libs.mysql.connector)
}

// WAR tasks we need to attach (keeps task providers reusable below)
val jsfWar = project(":jsf").tasks.named<War>("war")
val restWar = project(":rest").tasks.named<War>("war")

tasks.ear {
    // Match the Maven earName convention: use the root artifact id, Gradle appends the version
    archiveBaseName.set(rootProject.name)

    // Keep shared libs under /lib like defaultLibBundleDir
    libDirName = "lib"

    // Ensure WARs are built first
    dependsOn(jsfWar, restWar)

    // Place WARs at the EAR root
    from(jsfWar)
    from(restWar)

    // Generate application.xml with context roots similar to Maven maven-ear-plugin
    deploymentDescriptor {
        applicationName = "Pet Store"
        displayName = "Pet Store"
        description = "Manage pets, owners, and related records efficiently in one secure platform"

        webModule(jsfWar.get().archiveFileName.get(), "/pet-store/jsf")
        webModule(restWar.get().archiveFileName.get(), "/pet-store/rest")
    }
}
