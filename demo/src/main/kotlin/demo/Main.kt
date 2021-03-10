package demo

import com.github.dwursteisen.minigdx.GameApplicationBuilder
import com.github.dwursteisen.minigdx.GameConfiguration
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.Window
import proto.ProtoGame
import com.github.dwursteisen.minigdx.GameScreenConfiguration

@ExperimentalStdlibApi
class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val gameConfigurationFactory = {
                GameConfiguration(
                    gameName = "Demo",
                    window = Window(
                        width = 1024,
                        height = 728,
                        name = "Kotlin/JVM"
                    ),
                    gameScreenConfiguration = GameScreenConfiguration.WithRatio(16f / 9f)
                )
            }

            val gameFactory: (GameContext) -> Game = {
                val index = args.indexOf("--game")
                when (args.getOrElse(index + 1) { "" }) {
                    "bird" -> BirdGame(it)
                    "gravity" -> GravityGame(it)
                    "text" -> TextGame(it)
                    "sprite" -> SpriteGame(it)
                    "proto" -> ProtoGame(it)
                    "scene" -> SceneGame(it)
                    "trijam" -> TrijamGame(it)
                    "interpolation" -> InterpolationGame(it)
                    else -> BirdGame(it)
                }
            }

            GameApplicationBuilder(
                gameConfigurationFactory = gameConfigurationFactory,
                gameFactory = gameFactory

            ).start()
        }
    }
}
