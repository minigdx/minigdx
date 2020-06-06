package com.github.dwursteisen.minigdx.render

import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.EntityQuery
import com.github.dwursteisen.minigdx.shaders.StringFragmentShader
import com.github.dwursteisen.minigdx.shaders.StringVertexShader
import kotlin.test.Test
import kotlin.test.assertTrue

class RenderStageTest {

    @Test
    fun `add | it should add the camera`() {
        val engine = Engine()
        val stage = RenderStage(
            vertex = StringVertexShader(""),
            fragment = StringFragmentShader(""),
            query = EntityQuery()
        )

        engine.addSystem(stage)
        engine.create {
            add(Camera())
        }
        assertTrue(stage.entities.count() == 0)
        assertTrue(stage.camera != null)
    }
}
