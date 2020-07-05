package com.github.dwursteisen.minigdx.render

import com.curiouscreature.kotlin.math.Mat4
import com.dwursteisen.minigdx.scene.api.armature.Armature
import com.dwursteisen.minigdx.scene.api.armature.Frame
import com.dwursteisen.minigdx.scene.api.material.Material
import com.dwursteisen.minigdx.scene.api.model.Primitive
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.buffer.Buffer
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.gl
import com.github.dwursteisen.minigdx.shaders.FragmentShader
import com.github.dwursteisen.minigdx.shaders.ShaderProgram
import com.github.dwursteisen.minigdx.shaders.ShaderUtils
import com.github.dwursteisen.minigdx.shaders.TextureReference
import com.github.dwursteisen.minigdx.shaders.VertexShader

data class RenderOptions(
    val renderName: String,
    var renderOnDisk: Boolean
)

interface Stage

class Camera(val projection: Mat4) : Component

class Light : Component

class AnimatedModel(
    var animation: List<Frame>,
    val referencePose: Armature,
    val currentPose: Array<Mat4> = Array(40) { Mat4.identity() },
    var time: Float,
    val duration: Float
) : Component

class AnimatedMeshPrimitive(
    var isCompiled: Boolean = false,
    val primitive: Primitive,
    val material: Material,
    var verticesBuffer: Buffer? = null,
    var uvBuffer: Buffer? = null,
    var verticesOrderBuffer: Buffer? = null,
    var weightBuffer: Buffer? = null,
    var jointBuffer: Buffer? = null,
    var textureReference: TextureReference? = null
) : Component

class MeshPrimitive(
    var isCompiled: Boolean = false,
    val primitive: Primitive,
    val material: Material,
    var verticesBuffer: Buffer? = null,
    var uvBuffer: Buffer? = null,
    var verticesOrderBuffer: Buffer? = null,
    var textureReference: TextureReference? = null
) : Component

abstract class RenderStage<V : VertexShader, F : FragmentShader>(
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
