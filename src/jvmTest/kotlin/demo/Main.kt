package demo

import com.github.dwursteisen.minigdx.GLConfiguration
import com.github.dwursteisen.minigdx.configuration
import com.github.dwursteisen.minigdx.demo.DemoPlanet

class Main {

    companion object {

        @ExperimentalStdlibApi
        @JvmStatic
        fun main(args: Array<String>) {
            configuration(
                GLConfiguration(
                    name = "Kotin/JVM",
                    width = 800,
                    height = 800
                )
            ).run { DemoPlanet() }
        }
    }
}
