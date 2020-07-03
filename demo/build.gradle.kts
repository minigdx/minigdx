plugins {
    java
    application
    kotlin("jvm")
}

repositories {
    maven(url = "https://dl.bintray.com/dwursteisen/minigdx")
    maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":"))
}

application {
    mainClassName = "demo.Main"
    // applicationDefaultJvmArgs = listOf("-XstartOnFirstThread", "-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5000")
    applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
    executableDir = rootDir.resolve("src/commonMain/resources").absolutePath
}

project.tasks.getByName("run", JavaExec::class) {
    this.workingDir = rootDir.resolve("src/commonMain/resources").absoluteFile
    this.args = listOf("--game", "v2")
}
