import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    kotlin("jvm")
    id("com.github.dwursteisen.gltf") version "1.0.0-alpha8"
}

repositories {
    maven(url = "https://dl.bintray.com/dwursteisen/minigdx")
    maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    mavenCentral()
    mavenLocal()
    jcenter()
}

dependencies {
    implementation(project(":"))
    implementation(kotlin("stdlib-jdk8"))
}

gltfPlugin {
    create("assetsProtobuf") {
        this.gltfDirectory.set(project.projectDir.resolve("src/assets/v2"))
        this.target.set(project.projectDir.resolve("src/main/resources/v2"))
        this.format.set(com.github.dwursteisen.gltf.Format.PROTOBUF)
    }
}

application {
    mainClassName = "demo.Main"
    applicationDefaultJvmArgs = listOf("-XstartOnFirstThread", "-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5000")
    applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
    executableDir = projectDir.resolve("src/main/resources").absolutePath
}

project.tasks.getByName("run", JavaExec::class) {
    this.workingDir = projectDir.resolve("src/main/resources").absoluteFile
    this.args = listOf("--game", "gmtkjam")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
