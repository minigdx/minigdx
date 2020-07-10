package demo

import com.github.dwursteisen.minigdx.GLConfiguration
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.configuration
import com.github.dwursteisen.minigdx.game.GameSystem

@ExperimentalStdlibApi
class DemoApiV2(gameContext: GameContext) : GameSystem(gameContext, BirdScreen(gameContext))

@ExperimentalStdlibApi
class Gravity(gameContext: GameContext) : GameSystem(gameContext, GravityScreen(gameContext))

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
            ).execute {
                val index = args.indexOf("--game")
                when (args.getOrElse(index + 1) { "" }) {
                    "v2" -> DemoApiV2(it)
                    "gravity" -> Gravity(it)
                    else -> DemoApiV2(it)
                }
            }
        }
    }
}
