plugins {
    id("com.android.application")
    kotlin("multiplatform") version "1.3.70"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    id("com.github.dwursteisen.collada") version "1.0.0-alpha3"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven(
        url = uri("https://dl.bintray.com/dwursteisen/minigdx")
    )
    maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    google()
    mavenCentral()
    mavenLocal()
}

android {
    compileSdkVersion(29)
    buildToolsVersion = "29.0.3"
    defaultConfig {
        minSdkVersion(13)
    }
    sourceSets.getByName("main") {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        assets.srcDirs("src/commonMain/resources")
    }

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/license.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
        exclude("META-INF/notice.txt")
        exclude("META-INF/ASL2.0")
        exclude("META-INF/*.kotlin_module")
    }

    // Configure only for each module that uses Java 8
    // language features (either in its source code or
    // through dependencies).
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
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

    android("android") {
    }

    jvm {
        this.compilations.getByName("main").kotlinOptions.jvmTarget = "1.8"
        this.compilations.getByName("test").kotlinOptions.jvmTarget = "1.8"
    }
/*
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
*/
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("com.github.dwursteisen.kotlin-math:kotlin-math:1.0.0-alpha17")
                implementation("com.github.dwursteisen.collada:collada-api:1.0.0-alpha3")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("com.github.dwursteisen.kotlin-math:kotlin-math-js:1.0.0-alpha17")
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

        val jvmMain by getting {
            dependencies {
                api(kotlin("stdlib-jdk8"))
                implementation("com.github.dwursteisen.kotlin-math:kotlin-math-jvm:1.0.0-alpha17")

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

                implementation("org.l33tlabs.twl:pngdecoder:1.0")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("org.l33tlabs.twl:pngdecoder:1.0")
            }
        }

        val androidTest by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk7"))
                implementation(kotlin("test-junit"))
            }
        }
    }
}

colladaPlugin {
    create("assetsProtobuf") {
        this.gltfDirectory.set(project.projectDir.resolve("src/assets/v2"))
        this.target.set(project.projectDir.resolve("src/commonMain/resources/v2"))
        this.format.set(collada.Format.PROTOBUF as collada.Format)
    }
}

// -- convenient task to create the documentation.
project.tasks.create<Copy>("docs").apply {
    group = "minigdx"
    // package the application
    dependsOn("jsBrowserProductionWebpack")
    from("build/distributions/") {
        include("*.js", "*.protobuf", "*.png", "*.fnt")
    }
    into("docs")
}

// -- convenient tasks to test the game engine.
project.tasks.create("runJs").apply {
    group = "minigdx"
    dependsOn("jsBrowserDevelopmentRun")
}

project.tasks.create("runJvm").apply {
    group = "minigdx"
    dependsOn(":demo:run")
}

project.tasks.create("runAndroid").apply {
    group = "minigdx"
    dependsOn("installDebug")
}
