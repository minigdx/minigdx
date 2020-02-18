import threed.GLConfiguration
import threed.configuration

fun main() = configuration(
    GLConfiguration(
        title = "Kotlin/Native Metal",
        width = 800,
        height = 800
    )
).run { DemoGame() }
