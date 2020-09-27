import java.util.Date
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("multiplatform") version "1.3.70"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.5"
}

group = "com.github.dwursteisen.minigdx"
version = project.properties["version"] ?: "1.1-SNAPSHOT"

if (version == "unspecified") {
    version = "1.2-SNAPSHOT"
}

val kotlinMathVersion = "1.0.0-alpha18"
val gltfApiVersion = "1.0.0-alpha11"

val properties = Properties()
if (project.file("local.properties").exists()) {
    properties.load(project.file("local.properties").inputStream())
}

val bintrayUser = if (project.hasProperty("bintray_user")) {
    project.property("bintray_user") as? String
} else {
    System.getProperty("BINTRAY_USER")
}

val bintrayKey = if (project.hasProperty("bintray_key")) {
    project.property("bintray_key") as? String
} else {
    System.getProperty("BINTRAY_KEY")
}

repositories {
    maven(
        url = uri("https://dl.bintray.com/dwursteisen/minigdx")
    )
    maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    google()
    mavenCentral()
    jcenter()
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
        freeCompilerArgs = listOf("-Xopt-in=kotlin.ExperimentalStdlibApi")
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
                    this.freeCompilerArgs = listOf("-Xopt-in=kotlin.ExperimentalStdlibApi")
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

    metadata {
        this.compilations.all {
            this.kotlinOptions.freeCompilerArgs = listOf("-Xopt-in=kotlin.ExperimentalStdlibApi")
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlin("stdlib-common"))
                api("com.github.dwursteisen.kotlin-math:kotlin-math:$kotlinMathVersion")
                api("com.github.dwursteisen.gltf:gltf-api:$gltfApiVersion")
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
                implementation("com.github.dwursteisen.kotlin-math:kotlin-math-js:$kotlinMathVersion")
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
                implementation("com.github.dwursteisen.kotlin-math:kotlin-math-jvm:$kotlinMathVersion")

                val lwjglVersion = "3.2.3"
                val imguiVersion = "1.77-0.16"

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

                // https://github.com/SpaiR/imgui-java
                implementation("io.imgui.java:binding:$imguiVersion")
                implementation("io.imgui.java:lwjgl3:$imguiVersion")
                runtimeOnly("io.imgui.java:natives-linux:$imguiVersion")
                runtimeOnly("io.imgui.java:natives-macos:$imguiVersion")
                runtimeOnly("io.imgui.java:natives-windows:$imguiVersion")

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
project.tasks.create("runJvm").apply {
    group = "minigdx"
    dependsOn(":demo:run")
}

configure<com.jfrog.bintray.gradle.BintrayExtension> {
    user = properties.getProperty("bintray.user") ?: bintrayUser
    key = properties.getProperty("bintray.key") ?: bintrayKey
    publish = true
    if (findProperty("currentOs") == "macOS") {
        setPublications("jvm", "js", "macosX64", "iosArm64", "iosX64", "metadata")
    } else if (findProperty("currentOs") == "Windows") {
        setPublications("mingwX64")
    } else if (findProperty("currentOs") == "Linux") {
        setPublications("kotlinMultiplatform", "linuxX64")
    }
    pkg(delegateClosureOf<com.jfrog.bintray.gradle.BintrayExtension.PackageConfig> {
        repo = "minigdx"
        name = project.name
        githubRepo = "dwursteisen/mini-gdx.git"
        vcsUrl = "https://github.com/dwursteisen/mini-gdx.git"
        description = project.description
        setLabels("java")
        setLicenses("Apache-2.0")
        desc = description
        version(closureOf<com.jfrog.bintray.gradle.BintrayExtension.VersionConfig> {
            this.name = project.version.toString()
            released = Date().toString()
        })
    })
}

tasks.named("bintrayUpload") {
    dependsOn(":publishToMavenLocal")
}

tasks.withType<com.jfrog.bintray.gradle.tasks.BintrayUploadTask> {
    doFirst {
        project.publishing.publications
            .filterIsInstance<MavenPublication>()
            .forEach { publication ->
                val moduleFile = buildDir.resolve("publications/${publication.name}/module.json")
                if (moduleFile.exists()) {
                    publication.artifact(object :
                        org.gradle.api.publish.maven.internal.artifact.FileBasedMavenArtifact(moduleFile) {
                        override fun getDefaultExtension() = "module"
                    })
                }
            }
    }
}

project.afterEvaluate {
    project.publishing.publications.forEach {
        println("Available publication: ${it.name}")
    }
}

tasks.withType<Test> {
    this.testLogging {
        this.showStandardStreams = true
        this.exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}
