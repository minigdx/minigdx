package com.github.dwursteisen.minigdx.render

import MockLogger
import ModelFactory.gameContext
import com.curiouscreature.kotlin.math.Mat4
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Camera
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.graphics.GLResourceClient
import com.github.dwursteisen.minigdx.graphics.MockGL
import com.github.dwursteisen.minigdx.shaders.fragment.FragmentShader
import com.github.dwursteisen.minigdx.shaders.vertex.VertexShader
import kotlin.test.Test
import kotlin.test.assertTrue

class RenderStageTest {

    class EmptyVertexShader : VertexShader("")
    class EmptyFragmentShader : FragmentShader("")

    @Test
    fun add__it_should_add_the_camera() {
        val engine = Engine(gameContext())

        val vertex = EmptyVertexShader()
        val fragment = EmptyFragmentShader()
        val stage = object : RenderStage<EmptyVertexShader, EmptyFragmentShader>(
            gl = MockGL(),
            vertex = vertex,
            fragment = fragment,
            query = EntityQuery(Position::class),
            compiler = GLResourceClient(MockGL(), MockLogger())
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
