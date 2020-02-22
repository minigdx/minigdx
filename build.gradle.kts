plugins {
    java
    kotlin("multiplatform") version "1.3.70-eap-42"
    application
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    mavenCentral()
}

dependencies {
    testImplementation("junit", "junit", "4.12")
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
                implementation("com.github.dwursteisen.kotlin-math:kotlin-math:1.0.0-SNAPSHOT")
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
                implementation("com.github.dwursteisen.kotlin-math:kotlin-math-js:1.0.0-SNAPSHOT")
            }
        }

        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                // implementation("com.github.dwursteisen.kotlin-math:kotlin-math-jvm:1.0.0-SNAPSHOT")
                implementation(kotlin("stdlib-jdk8"))
                implementation("com.github.dwursteisen.kotlin-math:kotlin-math-jvm:1.0.0-SNAPSHOT")

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

application {
    mainClassName = "demo.Main"
}

project.tasks.getByName("run").dependsOn("jvmJar")
