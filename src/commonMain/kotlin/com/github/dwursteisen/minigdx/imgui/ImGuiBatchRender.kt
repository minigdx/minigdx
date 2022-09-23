package com.github.dwursteisen.minigdx.imgui

import com.curiouscreature.kotlin.math.ortho
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.components.LightComponent
import com.github.dwursteisen.minigdx.file.Texture
import com.github.dwursteisen.minigdx.shaders.Buffer
import com.github.dwursteisen.minigdx.shaders.DataSource
import com.github.dwursteisen.minigdx.shaders.ShaderProgram
import com.github.dwursteisen.minigdx.shaders.fragment.UVFragmentShader
import com.github.dwursteisen.minigdx.shaders.vertex.MeshVertexShader
import com.github.minigdx.imgui.ImGuiRenderer

class ImGuiBatchRender(
    private val gameContext: GameContext,
    private val vertex: MeshVertexShader,
    private val fragmentShader: UVFragmentShader
) : ImGuiRenderer<Texture> {

    private val gl = gameContext.gl

    private val verticesBuffer: Buffer = gl.createBuffer()
    private val verticesOrderBuffer: Buffer = gl.createBuffer()
    private val verticesUVsBuffer: Buffer = gl.createBuffer()
    private val normalsBuffer: Buffer = gl.createBuffer()

    lateinit var program: ShaderProgram

    override fun render(texture: Texture, vertices: FloatArray, uv: FloatArray, verticesOrder: IntArray) {

        val w = gameContext.gameScreen.width
        val h = gameContext.gameScreen.height
        val modelViewMatrix = ortho(
            // left
            l = 0f,
            // right
            r = w.toFloat(),
            // bottom
            b = h.toFloat(),
            // top
            t = 0f,
            // near
            n = 10f,
            // far
            f = -1f
        )

        gl.disable(GL.DEPTH_TEST)
        gl.enable(GL.BLEND)
        gl.blendFunc(GL.SRC_ALPHA, GL.ONE_MINUS_SRC_ALPHA)

        gl.bindBuffer(GL.ARRAY_BUFFER, verticesBuffer)
        gl.bufferData(
            target = GL.ARRAY_BUFFER,
            data = DataSource.FloatDataSource(vertices),
            usage = GL.STATIC_DRAW
        )

        gl.bindBuffer(GL.ARRAY_BUFFER, normalsBuffer)
        gl.bufferData(
            target = GL.ARRAY_BUFFER,
            data = DataSource.FloatDataSource(FloatArray(vertices.size)),
            usage = GL.STATIC_DRAW
        )

        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, verticesOrderBuffer)
        gl.bufferData(
            target = GL.ELEMENT_ARRAY_BUFFER,
            data = DataSource.ShortDataSource(verticesOrder.map { it.toShort() }.toShortArray()),
            usage = GL.STATIC_DRAW
        )

        gl.bindBuffer(GL.ARRAY_BUFFER, verticesUVsBuffer)
        gl.bufferData(
            target = GL.ARRAY_BUFFER,
            data = DataSource.FloatDataSource(uv),
            usage = GL.STATIC_DRAW
        )

        // ---- shader configuration ---- //

        // Configure the light.

        vertex.uLightColor.apply(program, LightComponent.TRANSPARENT_COLOR)
        vertex.uLightPosition.apply(program, LightComponent.ORIGIN)

        vertex.uModelView.apply(program, modelViewMatrix)
        vertex.aVertexPosition.apply(program, verticesBuffer)
        vertex.aVertexNormal.apply(program, normalsBuffer)
        vertex.aUVPosition.apply(program, verticesUVsBuffer)
        fragmentShader.uUV.apply(program, texture = texture.textureReference!!, unit = 0)

        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, verticesOrderBuffer)
        gl.drawElements(
            GL.TRIANGLES,
            verticesOrder.size,
            GL.UNSIGNED_SHORT,
            0
        )

        gl.disable(GL.BLEND)
        gl.enable(GL.DEPTH_TEST)
    }
}
