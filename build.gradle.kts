import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    java
    jacoco
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://repo.gradle.org/gradle/libs-releases")
    }
}

subprojects {
    if (name != "ear") {
        apply(plugin = "java")
    }

    group = "pablog"
    version = "1.0.0-SNAPSHOT"

    pluginManager.withPlugin("java") {
        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }

        tasks.withType<JavaCompile> {
            options.encoding = "UTF-8"
        }
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

    // Apply JaCoCo to all subprojects except tests and jsf
    val excludedFromCoverage = listOf("tests", "jsf", "ear")
    if (name !in excludedFromCoverage) {
        apply(plugin = "jacoco")

        jacoco {
            toolVersion = "0.8.13"
        }

        tasks.withType<Test>().configureEach {
            finalizedBy(tasks.named("jacocoTestReport"))
        }

        tasks.withType<JacocoReport>().configureEach {
            dependsOn(tasks.named("test"))

            reports {
                xml.required = true
                html.required = true
                csv.required = false
            }
        }
    }

    // Wire JaCoCo agent for Arquillian tests in rest and service modules to record coverage
    val modulesWithArquillian = listOf("rest", "service")
    if (name in modulesWithArquillian) {
        val jacocoVersion = extensions.getByType<JacocoPluginExtension>().toolVersion

        val jacocoRuntimeAgent = configurations.maybeCreate("jacocoRuntimeAgent")
        dependencies {
            add("jacocoRuntimeAgent", "org.jacoco:org.jacoco.agent:${jacocoVersion}:runtime")
        }

        tasks.withType<Test>().configureEach {
            val destFile = layout.buildDirectory.file("jacoco/test.exec")

            doFirst {
                val agentPath = jacocoRuntimeAgent.resolve().single()
                val dest = destFile.get().asFile

                dest.parentFile.mkdirs()

                // Make the agent string available for substitution in arquillian.xml
                val javaagent = "-javaagent:${agentPath.absolutePath}=destfile=${dest.absolutePath},append=true,output=file"
                systemProperty("jacoco.javaagent", javaagent)
                systemProperty("jacoco.destfile", dest.absolutePath)
            }
        }
    }
}

tasks.register<JacocoReport>("jacocoRootReport") {
    group = "Verification"
    description = "Aggregates JaCoCo coverage across all subprojects"

    val excludedProjects = listOf("tests", "jsf", "ear")
    val coveredProjects = subprojects.filter { it.name !in excludedProjects }

    dependsOn(coveredProjects.map { it.tasks.named("test") })

    executionData.from(coveredProjects.map { it.layout.buildDirectory.file("jacoco/test.exec") })

    val mainSourceSets = coveredProjects.mapNotNull {
        it.extensions.findByType(SourceSetContainer::class.java)?.findByName("main")
    }

    sourceDirectories.from(mainSourceSets.flatMap { it.allSource.srcDirs })
    classDirectories.from(mainSourceSets.map { it.output })

    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }

    doLast {
        logger.lifecycle("Aggregated coverage report: ${reports.html.outputLocation.get().asFile.resolve("index.html").absolutePath}")
    }
}