package demo

import com.curiouscreature.kotlin.math.Mat4
import com.dwursteisen.minigdx.scene.api.Scene
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactory
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.file.get
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.math.Interpolations

class Objs(val name: String, val origin: Mat4) : Component

class InterpolationSystem : System() {

    val objs by interested(EntityQuery.of(Objs::class))

    override fun update(delta: Seconds, entity: Entity) = Unit

    lateinit var target: Entity

    override fun onGameStarted(engine: Engine) {
        target = objs.sortedBy { it.get(Objs::class).name }[1]
    }

    var blend = 0f

    override fun update(delta: Seconds) {
        val sortedBy = objs.sortedBy { it.get(Objs::class).name }
        if (input.isKeyPressed(Key.ARROW_LEFT)) {
            target = sortedBy.first()
            blend += 0.1f
        } else if (input.isKeyPressed(Key.ARROW_RIGHT)) {
            target = sortedBy.last()
            blend -= 0.1f
        }
       // blend = Interpolations.lerp(1f, blend)

        val entity = sortedBy[1]
        entity.get(Position::class).setGlobalTransform(Interpolations.interpolate(
            sortedBy.first().get(Objs::class).origin,
            sortedBy.last().get(Objs::class).origin,
            blend

        ))


    }
}

@ExperimentalStdlibApi
class InterpolationGame(override val gameContext: GameContext) : Game {

    private val scene: Scene by gameContext.fileHandler.get("v2/interpolation.protobuf")

    override fun createEntities(entityFactory: EntityFactory) {
        scene.children.forEach {
            val e = entityFactory.createFromNode(it, scene)
            if (it.name.contains(".00")) {
                e.add(Objs(it.name, Mat4.fromColumnMajor(*it.transformation.matrix)))
            }
        }
    }

    override fun createSystems(engine: Engine): List<System> {
        return super.createSystems(engine) + listOf(
            InterpolationSystem()
        )
    }
}
