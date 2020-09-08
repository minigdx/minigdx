package demo

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.translation
import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.relation.ObjectType
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.ecs.createFromNode
import com.github.dwursteisen.minigdx.ecs.createModel
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.physics.AABBCollisionResolver
import com.github.dwursteisen.minigdx.ecs.physics.CollisionResolver
import com.github.dwursteisen.minigdx.ecs.physics.SATCollisionResolver
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.game.Screen
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.dwursteisen.minigdx.math.lerp

class GravityComponent(
    val acceleration: Vector3 = Vector3(0, 0, 0),
    val velocity: Vector3 = Vector3(0, 0, 0),
    val displacement: Vector3 = Vector3(0f, 0f, 0f)
) : Component

class ColliderComponent : Component

class GravitySystem(private val collisionResolution: CollisionResolver = AABBCollisionResolver()) :
    System(EntityQuery(GravityComponent::class)) {

    private val colliders: List<Entity> by interested(EntityQuery(ColliderComponent::class))

    override fun update(delta: Seconds, entity: Entity) {
        val gravity = entity.get(GravityComponent::class)

        gravity.velocity.x += gravity.acceleration.x * delta
        gravity.velocity.y += gravity.acceleration.y * delta
        gravity.velocity.z += gravity.acceleration.z * delta

        gravity.displacement.x = gravity.velocity.x * delta
        gravity.displacement.y = gravity.velocity.y * delta
        gravity.displacement.z = gravity.velocity.z * delta
        val position = entity.get(Position::class)

        val acceptedDisplacement = listOf(
            Float3(gravity.displacement.x, 0f, 0f),
            Float3(0f, gravity.displacement.y, 0f),
            Float3(0f, 0f, gravity.displacement.z)
        ).filterNot { displacement ->
            val expectedTransformation = position.transformation * translation(displacement)

            colliders.asSequence()
                .filter { it != entity }
                .any { entityB ->
                    val boxA = entity.get(BoundingBox::class)
                    val boxB = entityB.get(BoundingBox::class)
                    val collide = collisionResolution.collide(
                        boxA,
                        expectedTransformation,
                        boxB,
                        entityB.get(Position::class).transformation
                    )
                    updateColorIfCollide(collide, boxA, boxB)
                }
        }

        acceptedDisplacement.forEach {
            position.translate(it.x, it.y, it.z)
        }
    }

    private fun updateColorIfCollide(collide: Boolean, boxA: BoundingBox, boxB: BoundingBox): Boolean {
        boxA.touch = collide
        boxB.touch = collide
        return collide
    }
}

class PlayerMoveSystem(
    val input: InputHandler
) : System(EntityQuery(GravityComponent::class)) {

    lateinit var reset: Float3

    override fun add(entity: Entity): Boolean {
        if (entityQuery.accept(entity)) {
            val v = entity.get(Position::class).transformation.position
            reset = Float3(v.x, v.y, v.z)
        }
        return super.add(entity)
    }

    override fun update(delta: Seconds, entity: Entity) {
        val gravity = entity.get(GravityComponent::class)

        if (input.isKeyPressed(Key.ARROW_LEFT)) {
            gravity.acceleration.add(x = -6f * delta)
        } else if (input.isKeyPressed(Key.ARROW_RIGHT)) {
            gravity.acceleration.add(x = 6f * delta)
        } else {
            gravity.acceleration.x = lerp(-gravity.velocity.x, gravity.acceleration.x, 0.9f, delta)
        }

        if (input.isKeyPressed(Key.ARROW_UP)) {
            gravity.acceleration.add(z = -6f * delta)
        } else if (input.isKeyPressed(Key.ARROW_DOWN)) {
            gravity.acceleration.add(z = 6f * delta)
        } else {
            gravity.acceleration.z = lerp(-gravity.velocity.z, gravity.acceleration.z, 0.9f, delta)
        }

        if (input.isKeyPressed(Key.U)) {
            gravity.acceleration.add(y = 6f * delta)
        } else if (input.isKeyPressed(Key.D)) {
            gravity.acceleration.add(y = -6f * delta)
        } else {
            gravity.acceleration.y = lerp(-gravity.velocity.y, gravity.acceleration.y, 0.9f, delta)
        }

        if (input.isKeyJustPressed(Key.R)) {
            entity.get(Position::class).setTranslate(reset.x, reset.y, reset.z)
            gravity.acceleration.set(0, 0, 0)
            gravity.velocity.set(0, 0, 0)
        }
    }
}

@ExperimentalStdlibApi
class GravityScreen(override val gameContext: GameContext) : Screen {

    private val scene: Scene by gameContext.fileHandler.get("v2/gravity.protobuf")

    override fun createEntities(engine: Engine) {
        scene.children.forEach { model ->
            val entity = engine.createFromNode(model, gameContext, scene)
            if (model.name == "cube") {
                entity.add(GravityComponent())
            }
            if(model.type == ObjectType.MODEL) {
                entity.add(ColliderComponent())
            }
        }
    }

    override fun createSystems(engine: Engine): List<System> {
        return listOf(
            PlayerMoveSystem(gameContext.input),
            GravitySystem(SATCollisionResolver())
        )
    }
}
