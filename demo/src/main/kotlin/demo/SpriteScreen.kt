package demo

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.gl.SpritePrimitive
import com.github.dwursteisen.minigdx.ecs.createUICamera
import com.github.dwursteisen.minigdx.entity.primitives.Texture
import com.github.dwursteisen.minigdx.game.GameSystem
import com.github.dwursteisen.minigdx.game.Screen
import com.github.dwursteisen.minigdx.render.sprites.SpriteRenderStrategy

@ExperimentalStdlibApi
class SpriteScreen(override val gameContext: GameContext) : Screen {

    private val sprite: Texture by gameContext.fileHandler.get("pt_font.png")

    override fun createEntities(engine: Engine) {
        val spriteComponent = SpritePrimitive(
            texture = sprite,
            y = sprite.height,
            renderStrategy = SpriteRenderStrategy
        )
        gameContext.glResourceClient.compile("sprite", spriteComponent)

        engine.create {
            add(spriteComponent)
            add(Position().translate(x = 600, y = 300).setScale(x = 600, y = -600))
        }

        engine.createUICamera(gameContext)
    }
}

@ExperimentalStdlibApi
class SpriteGame(gameContext: GameContext) : GameSystem(gameContext, SpriteScreen(gameContext))
