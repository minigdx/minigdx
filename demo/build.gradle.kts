plugins {
    java
    application
    kotlin("jvm")
}

repositories {
    maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    maven(url = "https://maven.pkg.github.com/dwursteisen/kotlin-math") {
        this.credentials {
            this.username = System.getenv("GITHUB_USERNAME")
            this.password = System.getenv("GITHUB_TOKEN")
        }
    }
    maven(url = "https://maven.pkg.github.com/dwursteisen/collada-parser") {
        this.credentials {
            this.username = System.getenv("GITHUB_USERNAME")
            this.password = System.getenv("GITHUB_TOKEN")
        }
    }
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
    this.args = listOf("--game", "suzanne")
}
