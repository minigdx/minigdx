package com.github.dwursteisen.minigdx.demo

import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.inverse
import com.curiouscreature.kotlin.math.projection
import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.camera.PerspectiveCamera
import com.dwursteisen.minigdx.scene.api.model.Position as PP
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.buffer.DataSource
import com.github.dwursteisen.minigdx.ecs.Component
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.Entity
import com.github.dwursteisen.minigdx.ecs.EntityQuery
import com.github.dwursteisen.minigdx.ecs.Position
import com.github.dwursteisen.minigdx.ecs.System
import com.github.dwursteisen.minigdx.ecs.WithColor
import com.github.dwursteisen.minigdx.fileHandler
import com.github.dwursteisen.minigdx.game.GameSystem
import com.github.dwursteisen.minigdx.game.Screen
import com.github.dwursteisen.minigdx.gl
import com.github.dwursteisen.minigdx.render.Camera
import com.github.dwursteisen.minigdx.render.MeshPrimitive
import com.github.dwursteisen.minigdx.render.RenderStage
import com.github.dwursteisen.minigdx.shaders.DefaultShaders
import com.github.dwursteisen.minigdx.shaders.FragmentShader
import com.github.dwursteisen.minigdx.shaders.ShaderParameter
import com.github.dwursteisen.minigdx.shaders.ShaderParameter.AtributeVec3
import com.github.dwursteisen.minigdx.shaders.ShaderParameter.UniformMat4
import com.github.dwursteisen.minigdx.shaders.VertexShader

class Rotating : Component

class RotatingSystem : System(EntityQuery(Rotating::class)) {

    override fun update(delta: Seconds, entity: Entity) {
        entity[Position::class].forEach {
            it.rotateZ(10f * delta)
        }
    }
}

class WorldVertexShader : VertexShader(
    shader = DefaultShaders.simpleVertexShader
) {
    val uModelMatrix = UniformMat4("uModelMatrix")
    val uViewMatrix = UniformMat4("uViewMatrix")
    val uProjectionMatrix = UniformMat4("uProjectionMatrix")
    val aVertexPosition = AtributeVec3("aVertexPosition")

    override val parameters: List<ShaderParameter> = listOf(
        uModelMatrix,
        uViewMatrix,
        uProjectionMatrix,
        aVertexPosition
    )
}

class WorldFragmentShader : FragmentShader(DefaultShaders.simpleFragmentShader)

@ExperimentalStdlibApi
class DemoScreen : Screen {

    private val model: Scene by fileHandler.get("v2/triangle.protobuf")

    override fun createEntities(engine: Engine) {
        model.models.values.forEach { model ->
            engine.create {
                add(Rotating())
                add(
                    Position(
                        transformation = Mat4.fromColumnMajor(*model.transformation.matrix)
                    )
                )
                model.mesh.primitives.forEach {
                    add(MeshPrimitive(primitive = it))
                    add(WithColor())
                }
            }
        }

        val camera = model.perspectiveCameras.values.first() as PerspectiveCamera
        engine.create {
            add(
                Camera(
                    projection = projection(
                        fov = camera.fov,
                        ratio = 1f, // FIXME,
                        near = camera.near,
                        far = camera.far
                    )
                )
            )
            add(Position(transformation = Mat4.fromColumnMajor(*camera.transformation.matrix)))
        }
    }

    override fun createSystems(): List<System> {
        return listOf(RotatingSystem())
    }

    override fun createRenderStage(): List<WorldRenderStage> {
        return listOf(WorldRenderStage())
    }
}

fun List<PP>.asDatasource(): DataSource.FloatDataSource {
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

class WorldRenderStage : RenderStage<WorldVertexShader, WorldFragmentShader>(
    vertex = WorldVertexShader(),
    fragment = WorldFragmentShader(),
    query = EntityQuery(MeshPrimitive::class)
) {

    override fun compile(entity: Entity) {
        entity[MeshPrimitive::class].forEach { primitive ->
            primitive.verticesBuffer = gl.createBuffer()
            gl.bindBuffer(GL.ARRAY_BUFFER, primitive.verticesBuffer!!)

            gl.bufferData(
                target = GL.ARRAY_BUFFER,
                data = primitive.primitive.vertices.map { it.position }.asDatasource(),
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
        }
    }

    override fun uniforms() {
        camera?.run {
            // TODO: the combine could be computed here for beter performance
            vertex.uViewMatrix.apply(program, inverse(this[Position::class].first().transformation))
            vertex.uProjectionMatrix.apply(program, this[Camera::class].first().projection)
        }
    }

    override fun update(delta: Seconds, entity: Entity) {
        vertex.uModelMatrix.apply(program, entity[Position::class].first().transformation)

        entity[MeshPrimitive::class].forEach { primitive ->
            vertex.aVertexPosition.apply(program, primitive.verticesBuffer!!)

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
