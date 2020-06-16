package com.github.dwursteisen.minigdx.demo

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.perspective
import com.curiouscreature.kotlin.math.translation
import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.camera.PerspectiveCamera
import com.dwursteisen.minigdx.scene.api.model.Position as PP
import com.dwursteisen.minigdx.scene.api.model.UV
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.buffer.DataSource
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
import com.github.dwursteisen.minigdx.render.Camera
import com.github.dwursteisen.minigdx.render.MeshPrimitive
import com.github.dwursteisen.minigdx.render.RenderStage
import com.github.dwursteisen.minigdx.shaders.WorldFragmentShader
import com.github.dwursteisen.minigdx.shaders.WorldVertexShader

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

@ExperimentalStdlibApi
class DemoScreen : Screen {

    private val scene: Scene by fileHandler.get("v2/model.protobuf")

    override fun createEntities(engine: Engine) {
        scene.models.values.forEach { model ->
            engine.create {
                add(Rotating())
                add(
                    Position(
                        transformation = Mat4.fromColumnMajor(*model.transformation.matrix)
                    )
                )
                model.mesh.primitives.forEach { primitive ->
                    add(MeshPrimitive(
                        primitive = primitive,
                        material = scene.materials.values.first { it.id == primitive.materialId }
                    ))
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

    override fun createSystems(): List<System> {
        return listOf(RotatingSystem(), CameraSystem())
    }

    override fun createRenderStage(): List<WorldRenderStage> {
        return listOf(WorldRenderStage())
    }
}

fun List<PP>.positionsDatasource(): DataSource.FloatDataSource {
    return DataSource.FloatDataSource(FloatArray(this.size * 3) {
        val y = it % 3
        val x = (it - y) / 3
        when (y) {
            0 -> this[x].x
            1 -> this[x].y
            2 -> this[x].z
            else -> throw IllegalArgumentException("index '$it' not expected.")
        }
    })
}

fun List<UV>.uvDatasource(): DataSource.FloatDataSource {
    return DataSource.FloatDataSource(FloatArray(this.size * 2) {
        val y = it % 2
        val x = (it - y) / 2
        when (y) {
            0 -> this[x].x
            1 -> this[x].y
            else -> throw IllegalArgumentException("index '$it' not expected.")
        }
    })
}

class WorldRenderStage : RenderStage<WorldVertexShader, WorldFragmentShader>(
    vertex = WorldVertexShader(),
    fragment = WorldFragmentShader(),
    query = EntityQuery(MeshPrimitive::class)
) {

    override fun compile(entity: Entity) {
        entity[MeshPrimitive::class]
            .filter { !it.isCompiled }
            .forEach { primitive ->
                // Push the model
                primitive.verticesBuffer = gl.createBuffer()
                gl.bindBuffer(GL.ARRAY_BUFFER, primitive.verticesBuffer!!)

                gl.bufferData(
                    target = GL.ARRAY_BUFFER,
                    data = primitive.primitive.vertices.map { it.position }.positionsDatasource(),
                    usage = GL.STATIC_DRAW
                )

                primitive.verticesOrderBuffer = gl.createBuffer()
                gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, primitive.verticesOrderBuffer!!)
                gl.bufferData(
                    target = GL.ELEMENT_ARRAY_BUFFER,
                    data = DataSource.ShortDataSource(primitive.primitive.verticesOrder.map { it.toShort() }
                        .toShortArray()),
                    usage = GL.STATIC_DRAW
                )

                // Push the texture
                val textureReference = gl.createTexture()
                gl.bindTexture(GL.TEXTURE_2D, textureReference)

                gl.texImage2D(
                    GL.TEXTURE_2D,
                    0,
                    GL.RGBA,
                    GL.RGBA,
                    2,
                    primitive.material.height,
                    GL.UNSIGNED_BYTE,
                    primitive.material.data
                )
                gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MAG_FILTER, GL.NEAREST)
                gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MIN_FILTER, GL.NEAREST)

                primitive.textureReference = textureReference

                // Push UV coordinates
                primitive.uvBuffer = gl.createBuffer()
                gl.bindBuffer(GL.ARRAY_BUFFER, primitive.uvBuffer!!)

                gl.bufferData(
                    target = GL.ARRAY_BUFFER,
                    data = primitive.primitive.vertices.map { it.uv }.uvDatasource(),
                    usage = GL.STATIC_DRAW
                )

                primitive.isCompiled = true
            }
    }

    override fun update(delta: Seconds, entity: Entity) {
        val combined = camera?.let {
            val view = it[Position::class].first().transformation
            val projection = it[Camera::class].first().projection
            projection * view
        } ?: Mat4.identity()
        val model = entity[Position::class].first().transformation

        vertex.uModelView.apply(program, combined * model)

        entity[MeshPrimitive::class].forEach { primitive ->
            vertex.aVertexPosition.apply(program, primitive.verticesBuffer!!)
            vertex.aUVPosition.apply(program, primitive.uvBuffer!!)
            fragment.uUV.apply(program, primitive.textureReference!!, unit = 0)

            gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, primitive.verticesOrderBuffer!!)
            gl.drawElements(GL.TRIANGLES, primitive.primitive.verticesOrder.size, GL.UNSIGNED_SHORT, 0)
        }
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
