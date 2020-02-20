package demo

import DemoGame
import threed.GLConfiguration
import threed.configuration

class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            configuration(
                GLConfiguration(
                    name = "Kotin/JVM",
                    width = 800,
                    height = 800
                )
            ).run { DemoGame() }
        }
    }
}
