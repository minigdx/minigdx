@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.minigdx.mpp)
}

android {
    compileSdk = 29
    defaultConfig {
        minSdk = 21
    }
    sourceSets.getByName("main") {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        assets.srcDirs("src/commonMain/resources")
    }

    packagingOptions {
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
    this.commonMainApi(libs.minigdx.gltf.api)
    this.commonMainApi(libs.minigdx.imgui.light)
    this.commonMainApi(libs.kotlin.math.common)
    this.commonMainApi(libs.kotlin.coroutines)
    this.commonMainRuntimeOnly(libs.kotlin.reflect)

    this.jsMainApi(libs.kotlin.math.js)

    this.jvmMainApi(libs.kotlin.math.jvm)

    this.jvmMainImplementation(libs.lwjgl.core)
    this.jvmMainImplementation(variantOf(libs.lwjgl.core) { classifier("natives-windows") })
    this.jvmMainImplementation(variantOf(libs.lwjgl.core) { classifier("natives-linux") })
    this.jvmMainImplementation(variantOf(libs.lwjgl.core) { classifier("natives-macos") })

    this.jvmMainImplementation(libs.lwjgl.glfw)
    this.jvmMainImplementation(variantOf(libs.lwjgl.glfw) { classifier("natives-windows") })
    this.jvmMainImplementation(variantOf(libs.lwjgl.glfw) { classifier("natives-linux") })
    this.jvmMainImplementation(variantOf(libs.lwjgl.glfw) { classifier("natives-macos") })

    this.jvmMainImplementation(libs.lwjgl.opengl)
    this.jvmMainImplementation(variantOf(libs.lwjgl.opengl) { classifier("natives-windows") })
    this.jvmMainImplementation(variantOf(libs.lwjgl.opengl) { classifier("natives-linux") })
    this.jvmMainImplementation(variantOf(libs.lwjgl.opengl) { classifier("natives-macos") })

    this.jvmMainImplementation(libs.lwjgl.openal)
    this.jvmMainImplementation(variantOf(libs.lwjgl.openal) { classifier("natives-windows") })
    this.jvmMainImplementation(variantOf(libs.lwjgl.openal) { classifier("natives-windows-x86") })
    this.jvmMainImplementation(variantOf(libs.lwjgl.openal) { classifier("natives-linux") })
    this.jvmMainImplementation(variantOf(libs.lwjgl.openal) { classifier("natives-linux-arm32") })
    this.jvmMainImplementation(variantOf(libs.lwjgl.openal) { classifier("natives-linux-arm64") })
    this.jvmMainImplementation(variantOf(libs.lwjgl.openal) { classifier("natives-macos") })

    this.jvmMainImplementation(libs.misc.mp3)
    this.jvmMainImplementation(libs.misc.pngdecoder)

    this.androidMainApi(libs.kotlin.math.jvm)
    this.androidMainImplementation(libs.misc.pngdecoder)
}

minigdxDeveloper {
    this.name.set("miniGDX")
    this.description.set("Multiplatform 3D Game Engine using Kotlin Multiplatform")
    this.projectUrl.set("https://github.com/minigdx/minigdx")
    this.licence {
        name.set("MIT Licence")
        url.set("https://github.com/minigdx/minigdx/blob/master/LICENSE")
    }
    developer {
        name.set("David Wursteisen")
        email.set("david.wursteisen+minigdx@gmail.com")
        url.set("https://github.com/dwursteisen")
    }
}
