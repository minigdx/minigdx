package demo

import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.ortho
import com.curiouscreature.kotlin.math.perspective
import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.camera.OrthographicCamera
import com.dwursteisen.minigdx.scene.api.camera.PerspectiveCamera
import com.dwursteisen.minigdx.scene.api.model.Model
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.BoundingBox
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.game.Screen
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.dwursteisen.minigdx.render.Camera
import com.github.dwursteisen.minigdx.render.MeshPrimitive
import com.dwursteisen.minigdx.scene.api.camera.Camera as GltfCamera

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
            val entity = engine.createFrom(model, scene, gameContext)
            if (model.name == "cube") {
                entity.add(GravityComponent())
            }
            entity.add(ColliderComponent())
        }
    }

    override fun createSystems(): List<System> {
        return listOf(GravitySystem())
    }
}

@ExperimentalStdlibApi
fun Engine.createFrom(model: Model, scene: Scene, context: GameContext): Entity {
    return this.create {
        if (model.armatureId < 0) {
            model.mesh.primitives.forEach { primitive ->
                add(MeshPrimitive(
                    primitive = primitive,
                    material = scene.materials.values.first { it.id == primitive.materialId }
                ))
            }
            val transformation = Mat4.fromColumnMajor(*model.transformation.matrix)
            add(Position(transformation))
            model.boxes.forEach { add(BoundingBox.from(it)) }
        } else {
            throw IllegalArgumentException("Animated model is not supported yet")
        }
    }
}

fun Engine.createFrom(camera: GltfCamera, context: GameContext): Entity {
    val cameraComponent = when (camera) {
        is PerspectiveCamera -> Camera(
            projection = perspective(
                fov = camera.fov,
                aspect = context.ratio,
                near = camera.near,
                far = camera.far
            )
        )
        is OrthographicCamera -> {
            val width = context.gl.screen.width / camera.scale
            val height = context.gl.screen.height / camera.scale
            Camera(
                projection = ortho(
                    l = width * -0.5f,
                    r = width * 0.5f,
                    b = height * -0.5f,
                    t = height * 0.5f,
                    n = camera.near,
                    f = camera.far
                )
            )
        }
        else -> throw IllegalArgumentException("${camera::class} is not supported")
    }

    return this.create {
        add(cameraComponent)
        add(Position(Mat4.fromColumnMajor(*camera.transformation.matrix), way = -1f))
    }
}
