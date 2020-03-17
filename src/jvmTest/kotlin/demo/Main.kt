package demo

import com.github.dwursteisen.minigdx.DemoPlanet
import com.github.dwursteisen.minigdx.GLConfiguration
import com.github.dwursteisen.minigdx.configuration

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
            ).run { DemoPlanet() }
        }
    }
}
