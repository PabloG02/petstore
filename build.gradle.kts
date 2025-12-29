plugins {
    java
}

group = "pablog"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
        maven("https://repo.gradle.org/gradle/libs-releases")
    }
}

subprojects {
    apply(plugin = "java")

    group = "pablog"
    version = "1.0.0-SNAPSHOT"

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<Test>().configureEach {
        // Prefer Gradle property `jboss.home`, fall back to JBOSS_HOME env var
        val jbossHome = providers.gradleProperty("jboss.home")
            .orElse(providers.environmentVariable("JBOSS_HOME"))
            .orNull

        require(!jbossHome.isNullOrBlank()) {
            "Set jboss.home (Gradle property) or JBOSS_HOME (env var) to your WildFly path"
        }

        systemProperty("jboss.home", jbossHome)
    }
}