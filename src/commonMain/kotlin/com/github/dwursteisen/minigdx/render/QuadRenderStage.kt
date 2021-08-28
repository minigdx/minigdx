package com.github.dwursteisen.minigdx.render

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.graphics.TextureFrameBuffer
import com.github.dwursteisen.minigdx.shaders.DataSource
import com.github.dwursteisen.minigdx.shaders.fragment.FragmentShader
import com.github.dwursteisen.minigdx.shaders.vertex.QuadShader
import com.github.dwursteisen.minigdx.utils.MeshFactoryUtils

class QuadRenderStage(
    gameContext: GameContext,
    fragmentShader: FragmentShader,
    val textureFrameBuffer: TextureFrameBuffer<out FragmentShader>
) : RenderStage<QuadShader, FragmentShader>(
    gameContext,
    QuadShader(),
    fragmentShader,
    EntityQuery.none()
) {

    val quad = MeshFactoryUtils.createPlane()

    val quadBuffer = gl.createBuffer()
    val quadOrder = gl.createBuffer()
    var size = 0

    override fun onGameStarted(engine: Engine) {
        gl.bindBuffer(GL.ARRAY_BUFFER, quadBuffer)
        val vertices = quad.vertices
            .map { it.position }
            .flatMap { listOf(it.x, it.y) }
            .toFloatArray()

        gl.bufferData(
            target = GL.ARRAY_BUFFER,
            data = DataSource.FloatDataSource(vertices),
            usage = GL.STATIC_DRAW
        )
        val order = quad.verticesOrder.map { it.toShort() }.toShortArray()

        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, quadOrder)
        gl.bufferData(
            target = GL.ELEMENT_ARRAY_BUFFER,
            data = DataSource.ShortDataSource(order),
            usage = GL.STATIC_DRAW
        )
        size = order.size
    }

    override fun update(delta: Seconds) {
        super.update(delta)
        vertex.aVertexPosition.apply(program, quadBuffer)
        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, quadOrder)

        textureFrameBuffer.updateFragmentShader(delta)

        gl.drawElements(
            GL.TRIANGLES,
            size,
            GL.UNSIGNED_SHORT,
            0
        )
    }

    override fun update(delta: Seconds, entity: Entity) = Unit
}
