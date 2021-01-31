package demo

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactory
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.file.Texture
import com.github.dwursteisen.minigdx.game.GameWrapper
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.file.get

@ExperimentalStdlibApi
class SpriteGame(override val gameContext: GameContext) : Game {

    private val sprite: Texture by gameContext.fileHandler.get("pt_font.png")

    override fun createEntities(entityFactory: EntityFactory) {
        entityFactory.create {
            add(Position().setGlobalTranslation(x = 600, y = 300).setScale(x = 300, y = 300))
        }

        entityFactory.createUICamera()
    }
}
