import threed.GLConfiguration
import threed.configuration

fun main() = configuration(GLConfiguration(
    name = "Kotin/JVM",
    width = 800,
    height = 800
)).run { DemoGame() }
