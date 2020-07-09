package demo

import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.perspective
import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.camera.PerspectiveCamera
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.GLConfiguration
import com.github.dwursteisen.minigdx.configuration
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.BoundingBox
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.fileHandler
import com.github.dwursteisen.minigdx.game.GameSystem
import com.github.dwursteisen.minigdx.game.Screen
import com.github.dwursteisen.minigdx.log
import com.github.dwursteisen.minigdx.render.AnimatedMeshPrimitive
import com.github.dwursteisen.minigdx.render.AnimatedModel
import com.github.dwursteisen.minigdx.render.Camera

@ExperimentalStdlibApi
class DemoScreen(private val screen: com.github.dwursteisen.minigdx.Screen) : Screen {

    private val bird: Scene by fileHandler.get("v2/bird.protobuf")

    override fun createEntities(engine: Engine) {
        bird.models.values.forEach { model ->
            log.info("DEMO") { "Create animated model '${model.name}'" }
            engine.create {
                val animation = bird.animations[0]!!.last().frames
                add(
                    AnimatedModel(
                        animation = animation,
                        referencePose = bird.armatures.values.first(),
                        time = 0f,
                        duration = animation.maxBy { it.time }?.time ?: 0f
                    )
                )
                model.mesh.primitives.forEach { primitive ->
                    add(
                        AnimatedMeshPrimitive(
                            primitive = primitive,
                            material = bird.materials.values.first { it.id == primitive.materialId }
                        )
                    )
                }
                add(Position(Mat4.fromColumnMajor(*model.transformation.matrix)))
                add(BoundingBox.from(model.mesh))
            }
        }

        bird.perspectiveCameras.values.forEach { camera ->
            camera as PerspectiveCamera
            log.info("DEMO") { "Create Camera model '${camera.name}'" }
            engine.create {
                add(
                    Camera(
                        projection = perspective(
                            fov = camera.fov,
                            aspect = screen.ratio,
                            near = camera.near,
                            far = camera.far
                        )
                    )
                )
                add(Position(Mat4.fromColumnMajor(*camera.transformation.matrix), way = -1f))
            }
        }
    }
}

@ExperimentalStdlibApi
class DemoApiV2(gl: GL) : GameSystem(gl, DemoScreen(gl.screen))

@ExperimentalStdlibApi
class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            configuration(
                GLConfiguration(
                    name = "Kotin/JVM",
                    width = 800,
                    height = 800
                )
            ).run {
                val index = args.indexOf("--game")
                when (args.getOrElse(index + 1) { "" }) {
                    "v2" -> DemoApiV2(it)
                    else -> DemoApiV2(it)
                }
            }
        }
    }
}
