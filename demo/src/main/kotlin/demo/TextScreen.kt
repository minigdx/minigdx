package demo

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.TextComponent
import com.github.dwursteisen.minigdx.ecs.components.UIComponent
import com.github.dwursteisen.minigdx.ecs.createModel
import com.github.dwursteisen.minigdx.ecs.createUICamera
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.file.Font
import com.github.dwursteisen.minigdx.game.GameSystem
import com.github.dwursteisen.minigdx.game.Screen
import kotlin.math.cos

@ExperimentalStdlibApi
class TextScreen(override val gameContext: GameContext) : Screen {

    private val font: Font by gameContext.fileHandler.get("pt_font")

    lateinit var txt: Entity

    override fun createEntities(engine: Engine) {
        txt = engine.createModel(font, "Example Of Text", 50f, 50f)
        txt.add(UIComponent())
        engine.createUICamera(gameContext)
    }

    override fun createSystems(engine: Engine): List<System> {
        return listOf(
            object : System(EntityQuery(TextComponent::class)) {

                var time = 0f
                override fun update(delta: Seconds, entity: Entity) {
                    time += delta

                    entity.get(Position::class).setGlobalTranslation(x = cos(time) * 300f)
                }
            }
        )
    }
}

@ExperimentalStdlibApi
class TextGame(gameContext: GameContext) : GameSystem(gameContext, TextScreen(gameContext))
