rootProject.name = "3d-skeleton"

pluginManagement {
    repositories {
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
        gradlePluginPortal()
        maven(url = "https://maven.pkg.github.com/dwursteisen/collada-parser") {
            this.credentials {
                this.username = System.getenv("GITHUB_USERNAME")
                this.password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

include("demo")
