plugins {
    kotlin("jvm") version "2.1.10"
}

group = "br.com.gabs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.javalin:javalin:6.5.0")
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("dev.langchain4j:langchain4j:1.0.0-beta2")
    implementation("dev.langchain4j:langchain4j-qdrant:1.0.0-beta2")
    implementation("dev.langchain4j:langchain4j-ollama:1.0.0-beta2")
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}