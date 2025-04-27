import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.10"
    application
}

group = "com.team1091"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jogamp.org/deployment/maven")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.processing:core:4.4.1")
}

application {
    mainClass = "com.team1091.tanks.MainKt"
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(22)
}