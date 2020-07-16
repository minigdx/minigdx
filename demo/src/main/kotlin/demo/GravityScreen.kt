package demo

import com.dwursteisen.minigdx.scene.api.Scene
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.BoundingBox
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.createFrom
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.game.Screen
import com.github.dwursteisen.minigdx.math.Vector3

class GravityComponent(
    var gravity: Vector3 = Vector3(0, -1, 0),
    var displacement: Vector3 = Vector3(0f, 0f, 0f)
) : Component

class ColliderComponent : Component

class GravitySystem : System(EntityQuery(GravityComponent::class)) {

    private val colliders: List<Entity> by interested(EntityQuery(ColliderComponent::class))

    override fun update(delta: Seconds, entity: Entity) {
        val gravity = entity.get(GravityComponent::class)
        gravity.displacement.x = gravity.gravity.x * delta
        gravity.displacement.y = gravity.gravity.y * delta
        gravity.displacement.z = gravity.gravity.z * delta
        val position = entity.get(Position::class)
        val translation = position.translation
        val expectedPosition = Vector3(
            gravity.displacement.x + translation.x,
            gravity.displacement.y + translation.y,
            gravity.displacement.z + translation.z
        )
        val hasTouch = colliders.asSequence()
            .filter { it != entity }
            .any { it.overlaps(expectedPosition, entity) }

        if (!hasTouch) {
            position.translate(gravity.displacement)
        }
    }
}

fun Entity.overlaps(positionB: Vector3, target: Entity): Boolean {
    val boxA = this.get(BoundingBox::class)
    val positionA = this.get(Position::class).translation
    val boxB = target.get(BoundingBox::class)

    val minXA = boxA.vertices.minBy { it.position.x }!!.position.x + positionA.x
    val maxXA = boxA.vertices.maxBy { it.position.x }!!.position.x + positionA.x
    val minYA = boxA.vertices.minBy { it.position.y }!!.position.y + positionA.y
    val maxYA = boxA.vertices.maxBy { it.position.y }!!.position.y + positionA.y
    val minZA = boxA.vertices.minBy { it.position.z }!!.position.z + positionA.z
    val maxZA = boxA.vertices.maxBy { it.position.x }!!.position.z + positionA.z


    val minXB = boxB.vertices.minBy { it.position.x }!!.position.x + positionB.x
    val maxXB = boxB.vertices.maxBy { it.position.x }!!.position.x + positionB.x
    val minYB = boxB.vertices.minBy { it.position.y }!!.position.y + positionB.y
    val maxYB = boxB.vertices.maxBy { it.position.y }!!.position.y + positionB.y
    val minZB = boxB.vertices.minBy { it.position.z }!!.position.z + positionB.z
    val maxZB = boxB.vertices.maxBy { it.position.x }!!.position.z + positionB.z

    return (minXA < maxXB && maxXA > minXB &&
            minYA < maxYB && maxYA > minYB &&
            minZA < maxZB && maxZA > minZB)
}

@ExperimentalStdlibApi
class GravityScreen(override val gameContext: GameContext) : Screen {

    private val scene: Scene by gameContext.fileHandler.get("v2/gravity.protobuf")

    override fun createEntities(engine: Engine) {
        scene.perspectiveCameras.values.forEach { camera ->
            engine.createFrom(camera, gameContext)
        }

        scene.models.values.forEach { model ->
            val entity = engine.createFrom(model, scene)
            if (model.name == "cube") {
                entity.add(GravityComponent())
            }
            entity.add(ColliderComponent())
        }
    }

    override fun createSystems(engine: Engine): List<System> {
        return listOf(GravitySystem())
    }
}
