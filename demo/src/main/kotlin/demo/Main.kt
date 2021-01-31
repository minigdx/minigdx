package demo

import com.github.dwursteisen.minigdx.GameApplicationBuilder
import com.github.dwursteisen.minigdx.GameConfiguration
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.game.GameWrapper
import proto.ProtoGame


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
                    "bird" -> BirdGame(it)
                    "gravity" -> GravityGame(it)
                    "text" -> TextGame(it)
                    "sprite" -> SpriteGame(it)
                    "proto" -> ProtoGame(it)
                    "scene" -> SceneGame(it)
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
