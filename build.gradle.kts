plugins {
    java
    kotlin("multiplatform") version "1.3.60"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    id("com.github.dwursteisen.collada") version "1.0-SNAPSHOT"
}

group = "org.example"
version = "1.0-SNAPSHOT"

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
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
kotlin {
    /* Targets configuration omitted. 
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */

    js {
        this.useCommonJs()
        this.browser {
            this.webpackTask {
                this.compilation.kotlinOptions {
                    this.sourceMap = true
                    this.sourceMapEmbedSources = "always"
                }
            }
        }
        this.nodejs
    }

    jvm {
        this.compilations.getByName("main").kotlinOptions.jvmTarget = "1.8"
    }

    macosX64() {
        binaries {
            executable {
                // Change to specify fully qualified name of your application's entry point:
                entryPoint = "main"
                // Specify command-line arguments, if necessary:
                runTask?.args("")
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("com.github.dwursteisen.kotlin-math:kotlin-math:1.0-SNAPSHOT")
                implementation("com.github.dwursteisen.collada:collada-api:1.0-SNAPSHOT")
                // implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:0.14.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        js().compilations["main"].defaultSourceSet {
            dependencies {
                // implementation("com.github.dwursteisen.kotlin-math:kotlin-math-js:1.0.0-SNAPSHOT")
                implementation(kotlin("stdlib-js"))
                implementation("com.github.dwursteisen.kotlin-math:kotlin-math-js:1.0-SNAPSHOT")
            }
        }

        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                // implementation("com.github.dwursteisen.kotlin-math:kotlin-math-jvm:1.0.0-SNAPSHOT")
                api(kotlin("stdlib-jdk8"))
                implementation("com.github.dwursteisen.kotlin-math:kotlin-math-jvm:1.0-SNAPSHOT")

                val lwjglVersion = "3.2.3"
                implementation("org.lwjgl:lwjgl:$lwjglVersion")
                implementation("org.lwjgl:lwjgl:$lwjglVersion:natives-windows")
                implementation("org.lwjgl:lwjgl:$lwjglVersion:natives-linux")
                implementation("org.lwjgl:lwjgl:$lwjglVersion:natives-macos")
                implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
                implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion:natives-windows")
                implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion:natives-linux")
                implementation("org.lwjgl:lwjgl-glfw:$lwjglVersion:natives-macos")
                implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")
                implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion:natives-windows")
                implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion:natives-linux")
                implementation("org.lwjgl:lwjgl-opengl:$lwjglVersion:natives-macos")
            }
        }
    }
}

collada {
    create("assets") {
        this.daeDirectory.set(project.projectDir.resolve("src"))
        this.target.set(project.projectDir.resolve("src/commonMain/resources"))
    }
}

project.tasks.getByName("processResources").dependsOn("collada")
