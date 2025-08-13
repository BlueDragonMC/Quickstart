plugins {
    kotlin("jvm") version "2.0.0"
}

group = "com.bluedragonmc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven("https://reposilite.atlasengine.ca/public")
    }
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

// Copy all built game JARs to the root build folder
tasks.register("copyJars", Copy::class) {
    subprojects.forEach { subproject ->
        if (subproject.name == "common") return@forEach // Exclude the `common` project because it's a library, not a game
        dependsOn(subproject.tasks.build) // Ensure this task runs after the subproject builds
        from("${subproject.layout.buildDirectory.get()}/libs/${subproject.name}-1.0-SNAPSHOT.jar")
    }
    into("${layout.buildDirectory.get()}/all-jars")
}

// After the whole project is built, run the task we just created to copy the JARs
tasks.build.configure {
    finalizedBy(tasks["copyJars"])
}

// For development: copy build game JARs into the `run` folder
tasks.register("copyDev", Copy::class) {
    dependsOn("copyJars")
    from("${layout.buildDirectory.get()}/all-jars")
    into("${projectDir}/run/games/")
}

// For development: build the sibling `Server` project
tasks.register("buildServerDev", Exec::class) {
    workingDir = File(projectDir.parent, "Server")
    commandLine = listOf("../Server/gradlew", "build", "-x", "test")
}

// For development: copy the `Server` project artifact to the `run` folder
tasks.register("copyServerDev", Copy::class) {
    dependsOn("buildServerDev")
    from("${projectDir}/../Server/build/libs/Server-1.0-SNAPSHOT-all.jar")
    into("${projectDir}/run")
    rename { "server.jar" }
}

tasks.register("cleanRunFolder", Delete::class) {
    delete("${projectDir}/run/games", "${projectDir}/run/server.jar")
}

tasks.clean.configure {
    dependsOn(tasks["cleanRunFolder"])
}

// For development: uses the outputs of the above tasks to start a dev server
tasks.register("runDev", Exec::class) {
    dependsOn(tasks["copyServerDev"])
    dependsOn(tasks["copyDev"])
    workingDir = File(projectDir, "run")
    commandLine = listOf("java", "-jar", "${projectDir}/run/server.jar")
}