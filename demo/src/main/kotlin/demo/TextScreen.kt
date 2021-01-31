package demo

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.translation
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactory
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.TextComponent
import com.github.dwursteisen.minigdx.ecs.components.UIComponent
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.file.Font
import com.github.dwursteisen.minigdx.game.GameSystem
import com.github.dwursteisen.minigdx.game.Screen
import kotlin.math.cos
import com.github.dwursteisen.minigdx.file.get

@ExperimentalStdlibApi
class TextScreen(override val gameContext: GameContext) : Screen {

    private val font: Font by gameContext.fileHandler.get("pt_font")

    lateinit var txt: Entity

    override fun createEntities(entityFactory: EntityFactory) {
        txt = entityFactory.createText( "Example Of Text",font,  translation(Float3(0f, 0f, 0f)))
        txt.add(UIComponent())
        entityFactory.createUICamera()
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
