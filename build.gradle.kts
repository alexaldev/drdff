import java.io.FileOutputStream
import java.util.*

plugins {
    kotlin("jvm") version "1.8.0"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.13.4")

    implementation("com.github.ajalt.clikt:clikt:3.5.2")

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    implementation("org.slf4j:slf4j-simple:2.0.5")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    implementation(kotlin("stdlib-jdk8"))
}

java {
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "11"
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = "11"
}

tasks.test {

    useJUnitPlatform()

    testLogging {

        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        events = mutableSetOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
        )
        showStandardStreams = true
    }
}

val generatedVersionDir = "$buildDir/generated-version"

sourceSets {
    main {
        kotlin {
            output.dir(generatedVersionDir)
        }
    }
}

tasks.jar {
    doLast {
        manifest.attributes["Main-Class"] = "DriverKt"
        val dependencies = configurations
            .runtimeClasspath
            .get()
            .map(::zipTree)
        from(dependencies)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

tasks.register("generateVersionProperties") {
    doLast {
        val propertiesFile = file("$generatedVersionDir/version.properties")
        propertiesFile.parentFile.mkdirs()
        val properties = Properties()
        properties.setProperty("version", "$version")
        val out = FileOutputStream(propertiesFile)
        properties.store(out, null)
    }
}

tasks.named("processResources") {
    dependsOn("generateVersionProperties")
}
