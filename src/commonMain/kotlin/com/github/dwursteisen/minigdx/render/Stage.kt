package com.github.dwursteisen.minigdx.render

import com.curiouscreature.kotlin.math.Mat4
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Camera
import com.github.dwursteisen.minigdx.ecs.components.Light
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.shaders.ShaderProgram
import com.github.dwursteisen.minigdx.shaders.ShaderUtils
import com.github.dwursteisen.minigdx.shaders.fragment.FragmentShader
import com.github.dwursteisen.minigdx.shaders.vertex.VertexShader

data class RenderOptions(
    val renderName: String,
    var renderOnDisk: Boolean
)

interface Stage

abstract class RenderStage<V : VertexShader, F : FragmentShader>(
    protected val gl: GL,
    val vertex: V,
    val fragment: F,
    query: EntityQuery,
    val cameraQuery: EntityQuery = EntityQuery(
        Camera::class
    ),
    val lightsQuery: EntityQuery = EntityQuery(
        Light::class
    ),
    val renderOption: RenderOptions = RenderOptions("undefined", renderOnDisk = false)
) : Stage, System(query) {

    var lights: Sequence<Entity> = emptySequence()
        private set

    var camera: Entity? = null
        private set

    lateinit var program: ShaderProgram

    open val combinedMatrix: Mat4
        get() {
            return camera?.let {
                val view = it.get(Position::class).transformation
                val projection = it.get(Camera::class).projection
                projection * view
            } ?: Mat4.identity()
        }

    open fun compileShaders() {
        program = ShaderUtils.createShaderProgram(gl, vertex.toString(), fragment.toString()).apply {
            vertex.parameters.forEach {
                it.create(this)
            }
            fragment.parameters.forEach {
                it.create(this)
            }
        }
    }

    open fun uniforms() = Unit

    open fun uniforms(entity: Entity) = Unit

    open fun attributes(entity: Entity) = Unit

    override fun add(entity: Entity): Boolean {
        return if (cameraQuery.accept(entity)) {
            camera = entity
            true
        } else if (lightsQuery.accept(entity)) {
            val count = entities.count()
            lights += entity
            count != entities.count()
        } else {
            super.add(entity)
        }
    }

    override fun remove(entity: Entity): Boolean {
        return if (cameraQuery.accept(entity)) {
            camera = null
            true
        } else if (lightsQuery.accept(entity)) {
            val count = entities.count()
            lights -= entity
            count != entities.count()
        } else {
            super.remove(entity)
        }
    }

    override fun update(delta: Seconds) {
        // TODO: if renderInMemory -> FBO
        gl.useProgram(program)
        uniforms()
        super.update(delta)
    }

    abstract override fun update(delta: Seconds, entity: Entity)
}

class FrameBufferRegistry
