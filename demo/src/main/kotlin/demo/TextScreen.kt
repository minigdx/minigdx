package demo

import com.curiouscreature.kotlin.math.ortho
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.UICamera
import com.github.dwursteisen.minigdx.ecs.createFrom
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.entity.text.Font
import com.github.dwursteisen.minigdx.game.GameSystem
import com.github.dwursteisen.minigdx.game.Screen
import com.github.dwursteisen.minigdx.input.Key

@ExperimentalStdlibApi
class TextScreen(override val gameContext: GameContext) : Screen {

    private val font: Font by gameContext.fileHandler.get("pt_font")

    lateinit var txt: Entity

    override fun createEntities(engine: Engine) {
        txt = engine.createFrom(font, "Ceci est un exemple de text", 10f, 10f)

        engine.create {
//            val width = gameContext.gl.screen.width
//            val height = gameContext.gl.screen.height

            val width = 1000
            val height = 1000
            add(
                UICamera(
                    projection = ortho(
                        l = width * -0.5f,
                        r = width * 0.5f,
                        b = height * -0.5f,
                        t = height * 0.5f,
                        n = 0.1f,
                        f = 1f
                    )
                )
            )
            // put the camera in the center of the screen
            add(Position(way = -1f).translate(x = -width * 0.5f, y = -height * 0.5f))
        }
    }

    override fun render(engine: Engine, delta: Seconds) {
        super.render(engine, delta)
    }
}

@ExperimentalStdlibApi
class TextGame(gameContext: GameContext) : GameSystem(gameContext, TextScreen(gameContext))
