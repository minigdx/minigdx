package com.github.dwursteisen.minigdx.render

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Color
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.shaders.fragment.FragmentShader
import com.github.dwursteisen.minigdx.shaders.vertex.VertexShader

object EmptyVertexShader : VertexShader("")
object EmptyFragmentShader : FragmentShader("")

class ClearBufferRenderStage(
    gameContext: GameContext,
    private val clearColor: Color
) :
    RenderStage<EmptyVertexShader, EmptyFragmentShader>(
        gameContext = gameContext,
        vertex = EmptyVertexShader,
        fragment = EmptyFragmentShader,
        query = EntityQuery.none()
    ) {

    override fun compileShaders() = Unit

    override fun update(delta: Seconds) {
        gl.clearColor(clearColor.red, clearColor.green, clearColor.blue, clearColor.alpha)
        gl.clearDepth(1.0)
        gl.enable(GL.DEPTH_TEST)
        gl.depthFunc(GL.LEQUAL)
        gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)
    }

    override fun update(delta: Seconds, entity: Entity) = Unit
}
