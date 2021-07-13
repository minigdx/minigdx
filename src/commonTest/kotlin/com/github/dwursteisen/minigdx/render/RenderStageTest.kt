package com.github.dwursteisen.minigdx.render

import ModelFactory.gameContext
import com.github.dwursteisen.minigdx.GameScreen
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.CameraComponent
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.shaders.fragment.FragmentShader
import com.github.dwursteisen.minigdx.shaders.vertex.VertexShader
import kotlin.test.Test
import kotlin.test.assertTrue

class RenderStageTest {

    class EmptyVertexShader : VertexShader("")
    class EmptyFragmentShader : FragmentShader("")

    @Test
    fun add__it_should_add_the_camera() {
        val gameContext = gameContext()
        val engine = Engine(gameContext)

        val vertex = EmptyVertexShader()
        val fragment = EmptyFragmentShader()
        val stage = object : RenderStage<EmptyVertexShader, EmptyFragmentShader>(
            gameContext = gameContext,
            vertex = vertex,
            fragment = fragment,
            query = EntityQuery(Position::class),
        ) {
            override fun update(delta: Seconds, entity: Entity) = Unit

            override fun update(delta: Seconds) = Unit
        }

        engine.addSystem(stage)

        engine.create {
            add(
                CameraComponent(
                    gameScreen = GameScreen(1024, 1024),
                    type = CameraComponent.Type.PERSPECTIVE,
                    far = 0f,
                    near = 0f
                )
            )
        }
        engine.update(0f)

        assertTrue(stage.entities.count() == 0)
        assertTrue(stage.camera != null)
    }
}
