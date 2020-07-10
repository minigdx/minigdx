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
import com.github.dwursteisen.minigdx.render.Camera
import com.github.dwursteisen.minigdx.render.MeshPrimitive
import com.dwursteisen.minigdx.scene.api.camera.Camera as GltfCamera

class GravityComponent(
    var value: Float = -1f
) : Component

class GravitySystem : System(EntityQuery(GravityComponent::class)) {

    override fun update(delta: Seconds, entity: Entity) {
        val gravity = entity.get(GravityComponent::class)
        entity.get(Position::class).translate(y = gravity.value)
    }
}

@ExperimentalStdlibApi
class GravityScreen(private val context: GameContext) : Screen {

    private val scene: Scene by context.fileHandler.get("v2/gravity.protobuf")

    override fun createEntities(engine: Engine) {
        scene.perspectiveCameras.values.forEach { camera ->
            engine.createFrom(camera, context)
        }

        scene.models.values.forEach { model ->
            val entity = engine.createFrom(model, scene, context)
            if(model.name == "cube") {
                entity.add(GravityComponent())
            }
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
        is OrthographicCamera -> Camera(
            projection = ortho(
                l = context.gl.screen.width * -0.5f,
                r = context.gl.screen.width * 0.5f,
                b = context.gl.screen.height * -0.5f,
                t = context.gl.screen.height * 0.5f,
                n = camera.near,
                f = camera.far
            )
        )
        else -> throw IllegalArgumentException("${camera::class} is not supported")
    }

    return this.create {
        add(cameraComponent)
        add(Position(Mat4.fromColumnMajor(*camera.transformation.matrix), way = -1f))
    }
}
