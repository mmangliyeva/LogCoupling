plugins {
    kotlin("jvm") version "1.9.22"
    application
}

group = "org.example"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jsoup:jsoup:1.14.2")
    implementation("com.squareup.okhttp3:okhttp:4.9.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.23")
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("org.json:json:20210307")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(
            listOf(
                "compileJava",
                "compileKotlin",
                "processResources"
            )

        ) // We need this for Gradle optimization to work
        archiveClassifier.set("standalone") // Naming the jar
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        // Provided we set it up in the application plugin configuration
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) }
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar) // Trigger fat jar creation during build
    }
    test {
        useJUnitPlatform()
    }
}

application {
    mainClass.set("org.example.MainKt") // Specify the main class here
}
kotlin {
    jvmToolchain(17)
}