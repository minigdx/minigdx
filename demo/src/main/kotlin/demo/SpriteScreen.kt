package demo

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.createUICamera
import com.github.dwursteisen.minigdx.file.Texture
import com.github.dwursteisen.minigdx.game.GameSystem
import com.github.dwursteisen.minigdx.game.Screen

@ExperimentalStdlibApi
class SpriteScreen(override val gameContext: GameContext) : Screen {

    private val sprite: Texture by gameContext.fileHandler.get("pt_font.png")

    override fun createEntities(engine: Engine) {
        engine.create {
            add(Position().setGlobalTranslation(x = 600, y = 300).setScale(x = 300, y = 300))
        }

        engine.createUICamera(gameContext)
    }
}

@ExperimentalStdlibApi
class SpriteGame(gameContext: GameContext) : GameSystem(gameContext, SpriteScreen(gameContext))
