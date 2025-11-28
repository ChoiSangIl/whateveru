plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "1.9.0"
    application
}

application {
    mainClass.set("study.stdio.jsonrpc.Main")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val mcpVersion = "0.8.0"
val ktorVersion = "3.3.2"
val slf4jVersion = "2.0.9"

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.modelcontextprotocol:kotlin-sdk:$mcpVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:${ktorVersion}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
    implementation("org.slf4j:slf4j-nop:${slf4jVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "study.stdio.jsonrpc.MainKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}