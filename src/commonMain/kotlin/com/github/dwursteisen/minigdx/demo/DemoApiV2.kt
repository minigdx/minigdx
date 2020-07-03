package com.github.dwursteisen.minigdx.demo

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.perspective
import com.curiouscreature.kotlin.math.translation
import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.armature.Frame
import com.dwursteisen.minigdx.scene.api.camera.PerspectiveCamera
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Component
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.Entity
import com.github.dwursteisen.minigdx.ecs.EntityQuery
import com.github.dwursteisen.minigdx.ecs.Position
import com.github.dwursteisen.minigdx.ecs.System
import com.github.dwursteisen.minigdx.fileHandler
import com.github.dwursteisen.minigdx.game.GameSystem
import com.github.dwursteisen.minigdx.game.Screen
import com.github.dwursteisen.minigdx.gl
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.inputs
import com.github.dwursteisen.minigdx.render.AnimatedMeshPrimitive
import com.github.dwursteisen.minigdx.render.AnimatedMeshPrimitiveRenderStage
import com.github.dwursteisen.minigdx.render.AnimatedModel
import com.github.dwursteisen.minigdx.render.Camera
import com.github.dwursteisen.minigdx.render.RenderStage

class Rotating : Component

class CameraSystem : System(EntityQuery(Camera::class)) {
    override fun update(delta: Seconds, entity: Entity) {
        if (inputs.isKeyPressed(Key.Q)) {
            entity[Position::class].forEach {
                it.rotateY(1f * delta)
            }
        } else if (inputs.isKeyPressed(Key.D)) {
            entity[Position::class].forEach {
                it.rotateY(-1f * delta)
            }
        }

        if (inputs.isKeyPressed(Key.Z)) {
            entity[Position::class].forEach {
                it.transformation *= translation(Float3(0f, 0f, 50f * delta))
            }
        } else if (inputs.isKeyPressed(Key.S)) {
            entity[Position::class].forEach {
                it.transformation *= translation(Float3(0f, 0f, -50f * delta))
            }
        }
    }
}

class RotatingSystem : System(EntityQuery(Rotating::class)) {

    override fun update(delta: Seconds, entity: Entity) {
        entity[Position::class].forEach {
            it.rotateY(10f * delta)
        }
    }
}

class ArmatureUpdateSystem : System(EntityQuery(AnimatedModel::class)) {

    override fun update(delta: Seconds, entity: Entity) {
        entity[AnimatedModel::class].forEach {
            it.time += delta
            if (it.time > it.duration) {
                it.time = 0f
            }

            val currentFrame = it.animation.lastOrNull { f -> f.time <= it.time } ?: it.animation.first()

            (it.referencePose.joints.indices).forEach { index ->
                it.currentPose[index] = Mat4.fromColumnMajor(*currentFrame.globalTransformations[index].matrix) *
                        Mat4.fromColumnMajor(*it.referencePose.joints[index].inverseGlobalTransformation.matrix)
            }
        }
    }
}

@ExperimentalStdlibApi
class DemoScreen : Screen {

    private val scene: Scene by fileHandler.get("v2/elephant.protobuf")

    override fun createEntities(engine: Engine) {
        scene.models.values.forEach { model ->
            engine.create {
                add(Rotating())
                add(
                    Position(
                        transformation = Mat4.fromColumnMajor(*model.transformation.matrix)
                    )
                )

                if (model.armatureId >= 0) {
                    model.mesh.primitives.forEach { primitive ->
                        add(AnimatedMeshPrimitive(
                            primitive = primitive,
                            material = scene.materials.values.first { it.id == primitive.materialId }
                        ))
                    }
                    val armature = scene.armatures[model.armatureId]!!
                    val animation = scene.animations[model.armatureId]!!.first()
                    val frames: List<Frame> = animation.frames
                    add(
                        AnimatedModel(
                            time = 0f,
                            referencePose = armature,
                            animation = frames,
                            duration = animation.duration
                        )
                    )
                }
            }
        }

        val camera = scene.perspectiveCameras.values.first() as PerspectiveCamera
        engine.create {
            add(
                Camera(
                    projection = perspective(
                        fov = camera.fov,
                        aspect = 1f, // FIXME,
                        near = camera.near,
                        far = camera.far
                    )
                )
            )
            add(Position(transformation = Mat4.fromColumnMajor(*camera.transformation.matrix), way = -1f))
        }
    }

    override fun render(engine: Engine, delta: Seconds) {
        gl.clearColor(0f, 0f, 0f, 1f)
        gl.clearDepth(1.0)
        gl.enable(GL.DEPTH_TEST)
        gl.depthFunc(GL.LEQUAL)
        gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)

        engine.update(delta)
    }

    override fun createSystems(): List<System> {
        return listOf(ArmatureUpdateSystem(), RotatingSystem(), CameraSystem())
    }

    override fun createRenderStage(): List<RenderStage<*, *>> {
        return listOf(AnimatedMeshPrimitiveRenderStage())
    }
}

@ExperimentalStdlibApi
class DemoApiV2 : GameSystem() {

    init {
        screen = createScreen()
    }

    override fun createScreen(): Screen {
        return DemoScreen()
    }
}
