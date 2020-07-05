package com.github.dwursteisen.minigdx.render

import com.curiouscreature.kotlin.math.Mat4
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.shaders.FragmentShader
import com.github.dwursteisen.minigdx.shaders.VertexShader
import kotlin.test.Test
import kotlin.test.assertTrue

class RenderStageTest {

    class EmptyVertexShader : VertexShader("")
    class EmptyFragmentShader : FragmentShader("")

    @Test
    fun add__it_should_add_the_camera() {
        val engine = Engine()

        val vertex = EmptyVertexShader()
        val fragment = EmptyFragmentShader()
        val stage = object : RenderStage<EmptyVertexShader, EmptyFragmentShader>(
            vertex = vertex,
            fragment = fragment,
            query = EntityQuery()
        ) {
            override fun update(delta: Seconds, entity: Entity) = Unit
        }

        engine.addSystem(stage)
        engine.create {
            add(Camera(projection = Mat4.identity()))
        }
        assertTrue(stage.entities.count() == 0)
        assertTrue(stage.camera != null)
    }
}
