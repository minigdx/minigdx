package demo

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.translation
import com.dwursteisen.minigdx.scene.api.Scene
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.ecs.createFrom
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.physics.AABBCollisionResolver
import com.github.dwursteisen.minigdx.ecs.physics.CollisionResolver
import com.github.dwursteisen.minigdx.ecs.physics.SATCollisionResolver
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.game.Screen
import com.github.dwursteisen.minigdx.math.Vector3

class GravityComponent(
    var gravity: Vector3 = Vector3(0, -1, 0),
    var displacement: Vector3 = Vector3(0f, 0f, 0f)
) : Component

class ColliderComponent : Component

class GravitySystem(private val collisionResolution: CollisionResolver = AABBCollisionResolver()) :
    System(EntityQuery(GravityComponent::class)) {

    private val colliders: List<Entity> by interested(EntityQuery(ColliderComponent::class))

    override fun update(delta: Seconds, entity: Entity) {
        val gravity = entity.get(GravityComponent::class)
        gravity.displacement.x = gravity.gravity.x * delta
        gravity.displacement.y = gravity.gravity.y * delta
        gravity.displacement.z = gravity.gravity.z * delta
        val position = entity.get(Position::class)

        val expectedTransformation = position.transformation * translation(
            Float3(
                gravity.displacement.x,
                gravity.displacement.y,
                gravity.displacement.z
            )
        )

        val hasTouch = colliders.asSequence()
            .filter { it != entity }
            .any { entityB ->
                collisionResolution.collide(
                    entity.get(BoundingBox::class),
                    expectedTransformation,
                    entityB.get(BoundingBox::class),
                    entityB.get(Position::class).transformation
                )
            }

        // FIXME: if(touch) -> change color of BoudingBox?
        if (!hasTouch) {
            position.translate(gravity.displacement)
        }
    }
}

@ExperimentalStdlibApi
class GravityScreen(override val gameContext: GameContext) : Screen {

    private val scene: Scene by gameContext.fileHandler.get("v2/gravity.protobuf")

    override fun createEntities(engine: Engine) {
        scene.perspectiveCameras.values.forEach { camera ->
            engine.createFrom(camera, gameContext)
        }

        scene.models.values.forEach { model ->
            val entity = engine.createFrom(model, scene, gameContext)
            if (model.name == "cube") {
                entity.add(GravityComponent())
            }
            entity.add(ColliderComponent())
        }
    }

    override fun createSystems(engine: Engine): List<System> {
        return listOf(GravitySystem(collisionResolution = SATCollisionResolver()))
    }
}
