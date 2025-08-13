plugins {
    kotlin("jvm") version "2.0.0"
}

group = "com.bluedragonmc.quickstart"
version = "1.0-SNAPSHOT"

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(project(":common"))
    implementation(kotlin("stdlib-jdk8"))

    compileOnly(libs.server)
    compileOnly(libs.minestom)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}