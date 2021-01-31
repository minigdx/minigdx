package demo

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactory
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.file.Texture
import com.github.dwursteisen.minigdx.game.GameSystem
import com.github.dwursteisen.minigdx.game.Screen
import com.github.dwursteisen.minigdx.file.get

@ExperimentalStdlibApi
class SpriteScreen(override val gameContext: GameContext) : Screen {

    private val sprite: Texture by gameContext.fileHandler.get("pt_font.png")

    override fun createEntities(entityFactory: EntityFactory) {
        entityFactory.create {
            add(Position().setGlobalTranslation(x = 600, y = 300).setScale(x = 300, y = 300))
        }

        entityFactory.createUICamera()
    }
}

@ExperimentalStdlibApi
class SpriteGame(gameContext: GameContext) : GameSystem(gameContext, SpriteScreen(gameContext))
