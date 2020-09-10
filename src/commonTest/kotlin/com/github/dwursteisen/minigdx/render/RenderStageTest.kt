package com.github.dwursteisen.minigdx.render

import com.curiouscreature.kotlin.math.Mat4
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Camera
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.graphics.GLResourceClient
import com.github.dwursteisen.minigdx.graphics.MockGL
import com.github.dwursteisen.minigdx.logger.Logger
import com.github.dwursteisen.minigdx.shaders.fragment.FragmentShader
import com.github.dwursteisen.minigdx.shaders.vertex.VertexShader
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
            gl = MockGL(),
            vertex = vertex,
            fragment = fragment,
            query = EntityQuery(),
            compiler = GLResourceClient(MockGL(), object : Logger {
                override fun debug(tag: String, message: () -> String) {
                    TODO("Not yet implemented")
                }

                override fun debug(tag: String, exception: Throwable, message: () -> String) {
                    TODO("Not yet implemented")
                }

                override fun info(tag: String, message: () -> String) {
                    TODO("Not yet implemented")
                }

                override fun info(tag: String, exception: Throwable, message: () -> String) {
                    TODO("Not yet implemented")
                }

                override fun warn(tag: String, message: () -> String) {
                    TODO("Not yet implemented")
                }

                override fun warn(tag: String, exception: Throwable, message: () -> String) {
                    TODO("Not yet implemented")
                }

                override fun error(tag: String, message: () -> String) {
                    TODO("Not yet implemented")
                }

                override fun error(tag: String, exception: Throwable, message: () -> String) {
                    TODO("Not yet implemented")
                }

                override var rootLevel: Logger.LogLevel
                    get() = TODO("Not yet implemented")
                    set(value) {}
            })
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
