package demo

import com.github.dwursteisen.minigdx.GLConfiguration
import com.github.dwursteisen.minigdx.configuration
import com.github.dwursteisen.minigdx.demo.DemoAnimation
import com.github.dwursteisen.minigdx.demo.DemoAnimation2
import com.github.dwursteisen.minigdx.demo.DemoApiV2
import com.github.dwursteisen.minigdx.demo.DemoCamera
import com.github.dwursteisen.minigdx.demo.DemoGame
import com.github.dwursteisen.minigdx.demo.DemoKey
import com.github.dwursteisen.minigdx.demo.DemoLight
import com.github.dwursteisen.minigdx.demo.DemoPlanet
import com.github.dwursteisen.minigdx.demo.DemoSuzanne
import com.github.dwursteisen.minigdx.demo.DemoTexture

@ExperimentalStdlibApi
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
            ).run {
                val index = args.indexOf("--game")
                when (args.getOrElse(index + 1) { "" }) {
                    "animation" -> DemoAnimation()
                    "animation2" -> DemoAnimation2()
                    "2d" -> DemoTexture()
                    "key" -> DemoKey()
                    "suzanne" -> DemoSuzanne()
                    "game" -> DemoGame()
                    "camera" -> DemoCamera()
                    "light" -> DemoLight()
                    "v2" -> DemoApiV2()
                    else -> DemoPlanet()
                }
            }
        }
    }
}
