package com.github.dwursteisen.minigdx.render

import com.curiouscreature.kotlin.math.Mat4
import com.dwursteisen.minigdx.scene.api.model.Primitive
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.buffer.Buffer
import com.github.dwursteisen.minigdx.ecs.Component
import com.github.dwursteisen.minigdx.ecs.Entity
import com.github.dwursteisen.minigdx.ecs.EntityQuery
import com.github.dwursteisen.minigdx.ecs.System
import com.github.dwursteisen.minigdx.gl
import com.github.dwursteisen.minigdx.shaders.FragmentShader
import com.github.dwursteisen.minigdx.shaders.ShaderProgram
import com.github.dwursteisen.minigdx.shaders.ShaderUtils
import com.github.dwursteisen.minigdx.shaders.VertexShader

data class RenderOptions(
    val renderName: String,
    var renderOnDisk: Boolean
)

interface Stage

class Camera(val projection: Mat4) : Component

class Light : Component

class MeshPrimitive(
    val primitive: Primitive,
    var verticesBuffer: Buffer? = null,
    var verticesOrderBuffer: Buffer? = null
) : Component

abstract class RenderStage<V : VertexShader, F : FragmentShader>(
    val vertex: V,
    val fragment: F,
    query: EntityQuery,
    val cameraQuery: EntityQuery = EntityQuery(Camera::class),
    val lightsQuery: EntityQuery = EntityQuery(Light::class),
    val renderOption: RenderOptions = RenderOptions("undefined", renderOnDisk = false)
) : Stage, System(query) {

    var lights: Sequence<Entity> = emptySequence()
        private set

    var camera: Entity? = null
        private set

    lateinit var program: ShaderProgram

    fun compile() {
        program = ShaderUtils.createShaderProgram(vertex.toString(), fragment.toString()).apply {
            vertex.parameters.forEach {
                it.create(this)
            }
            fragment.parameters.forEach {
                it.create(this)
            }
        }
        entities.forEach {
            compile(it)
        }
    }

    open fun compile(entity: Entity) = Unit

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
            super.add(entity)
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
