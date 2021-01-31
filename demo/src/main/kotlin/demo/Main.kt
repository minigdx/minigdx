package demo

import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.GameApplicationBuilder
import com.github.dwursteisen.minigdx.GameConfiguration
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.game.GameSystem
import proto.ProtoGame

@ExperimentalStdlibApi
class DemoApiV2(gameContext: GameContext) : GameSystem(gameContext, BirdScreen(gameContext))

@ExperimentalStdlibApi
class Gravity(gameContext: GameContext) : GameSystem(gameContext, GravityScreen(gameContext))

@ExperimentalStdlibApi
class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val gameConfigurationFactory = {
                GameConfiguration(
                    name = "Kotlin/JVM",
                    gameName = "Demo",
                    width = 720,
                    height = 720
                )
            }

            val gameFactory: (GameContext) -> Game = {
                val index = args.indexOf("--game")
                when (args.getOrElse(index + 1) { "" }) {
                    "v2" -> DemoApiV2(it)
                    "gravity" -> Gravity(it)
                    "text" -> TextGame(it)
                    "sprite" -> SpriteGame(it)
                    "proto" -> ProtoGame(it)
                    "scene" -> SceneGame(it)
                    else -> DemoApiV2(it)
                }
            }

            GameApplicationBuilder(
                gameConfigurationFactory = gameConfigurationFactory,
                gameFactory = gameFactory

            ).start()
        }
    }
}
