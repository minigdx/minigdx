rootProject.name = "minigdx"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        maven {
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        }.mavenContent {
            includeVersionByRegex("com.github.minigdx", "(.*)", "LATEST-SNAPSHOT")
        }
        mavenLocal()
        jcenter()
    }
    // Replace the plugin com.android.application with a dependency with another name.
    // See https://medium.com/@StefMa/its-time-to-ditch-the-buildscript-block-a1ab12e0d9ce
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("com.android")) {
                useModule("com.android.tools.build:gradle:3.6.1")
            }
        }
    }
}
