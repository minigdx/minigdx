import threed.GLConfiguration
import threed.configuration

fun main() = configuration(GLConfiguration(canvasId = "canvas")).run { DemoGame() }
