

plugins {
    id("com.android.library")
    id("com.github.minigdx.gradle.plugin.developer.mpp") version "DEV-SNAPSHOT"
}

val kotlinMathVersion = "DEV-SNAPSHOT"
val gltfApiVersion = "DEV-SNAPSHOT"
val lwjglVersion = "3.2.3"
val imguiVersion = "1.77-0.16"

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

dependencies {
    this.commonMainApi("com.github.minigdx:gltf-api:$gltfApiVersion")
    this.commonMainApi("com.github.minigdx:kotlin-math:$kotlinMathVersion")

    this.jsMainApi("com.github.minigdx:kotlin-math-js:$kotlinMathVersion")

    this.jvmMainApi("com.github.minigdx:kotlin-math-jvm:$kotlinMathVersion")
    this.jvmMainImplementation("org.lwjgl:lwjgl:$lwjglVersion")
    this.jvmMainImplementation("org.lwjgl:lwjgl:$lwjglVersion:natives-windows")
    this.jvmMainImplementation("org.lwjgl:lwjgl:$lwjglVersion:natives-linux")
    this.jvmMainImplementation("org.lwjgl:lwjgl:$lwjglVersion:natives-macos")
    this.jvmMainImplementation("org.lwjgl:lwjgl-glfw:$lwjglVersion")
    this.jvmMainImplementation("org.lwjgl:lwjgl-glfw:$lwjglVersion:natives-windows")
    this.jvmMainImplementation("org.lwjgl:lwjgl-glfw:$lwjglVersion:natives-linux")
    this.jvmMainImplementation("org.lwjgl:lwjgl-glfw:$lwjglVersion:natives-macos")
    this.jvmMainImplementation("org.lwjgl:lwjgl-opengl:$lwjglVersion")
    this.jvmMainImplementation("org.lwjgl:lwjgl-opengl:$lwjglVersion:natives-windows")
    this.jvmMainImplementation("org.lwjgl:lwjgl-opengl:$lwjglVersion:natives-linux")
    this.jvmMainImplementation("org.lwjgl:lwjgl-opengl:$lwjglVersion:natives-macos")
    this.jvmMainImplementation("org.lwjgl:lwjgl-openal:$lwjglVersion")
    this.jvmMainImplementation("org.lwjgl:lwjgl-openal:$lwjglVersion:natives-linux")
    this.jvmMainImplementation("org.lwjgl:lwjgl-openal:$lwjglVersion:natives-linux-arm32")
    this.jvmMainImplementation("org.lwjgl:lwjgl-openal:$lwjglVersion:natives-linux-arm64")
    this.jvmMainImplementation("org.lwjgl:lwjgl-openal:$lwjglVersion:natives-macos")
    this.jvmMainImplementation("org.lwjgl:lwjgl-openal:$lwjglVersion:natives-windows")
    this.jvmMainImplementation("org.lwjgl:lwjgl-openal:$lwjglVersion:natives-windows-x86")

    // https://github.com/SpaiR/imgui-java
    this.jvmMainImplementation("io.imgui.java:binding:$imguiVersion")
    this.jvmMainImplementation("io.imgui.java:lwjgl3:$imguiVersion")
    this.jvmMainRuntimeOnly("io.imgui.java:natives-linux:$imguiVersion")
    this.jvmMainRuntimeOnly("io.imgui.java:natives-macos:$imguiVersion")
    this.jvmMainRuntimeOnly("io.imgui.java:natives-windows:$imguiVersion")

    this.jvmMainImplementation("fr.delthas:javamp3:1.0.1")
    this.jvmMainImplementation("org.l33tlabs.twl:pngdecoder:1.0")

    this.androidMainApi("com.github.minigdx:kotlin-math-jvm:$kotlinMathVersion")
    this.androidMainImplementation("org.l33tlabs.twl:pngdecoder:1.0")
}

// -- convenient tasks to test the game engine.
project.tasks.create("runJvm").apply {
    group = "minigdx"
    dependsOn(":demo:run")
}

tasks.withType<Test> {
    this.testLogging {
        this.showStandardStreams = true
        this.exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}
