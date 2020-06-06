package com.github.dwursteisen.minigdx.render

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Component
import com.github.dwursteisen.minigdx.ecs.Entity
import com.github.dwursteisen.minigdx.ecs.EntityQuery
import com.github.dwursteisen.minigdx.ecs.System
import com.github.dwursteisen.minigdx.shaders.FragmentShader
import com.github.dwursteisen.minigdx.shaders.VertexShader

data class RenderOptions(
    val renderName: String,
    var renderOnDisk: Boolean
)

interface Stage

class Camera : Component
class Light : Component

class RenderStage(
    val vertex: VertexShader,
    val fragment: FragmentShader,
    query: EntityQuery,
    val cameraQuery: EntityQuery = EntityQuery(Camera::class),
    val lightsQuery: EntityQuery = EntityQuery(Light::class),
    val renderOption: RenderOptions = RenderOptions("undefined", renderOnDisk = false)
) : Stage, System(query) {

    var lights: Sequence<Entity> = emptySequence()

    var camera: Entity? = null

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
        // TODO: set uniform
        super.update(delta)
    }

    override fun update(delta: Seconds, entity: Entity) {
        // TODO: render entity
    }
}

class FrameBufferRegistry
