package com.github.dwursteisen.minigdx.imgui

import com.curiouscreature.kotlin.math.Mat4
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.components.LightComponent
import com.github.dwursteisen.minigdx.file.Texture
import com.github.dwursteisen.minigdx.file.get
import com.github.dwursteisen.minigdx.shaders.DataSource
import com.github.dwursteisen.minigdx.shaders.ShaderProgram
import com.github.dwursteisen.minigdx.shaders.fragment.UVFragmentShader
import com.github.dwursteisen.minigdx.shaders.vertex.MeshVertexShader
import com.github.minigdx.imgui.ImGUIRenderer

class ImGUI(
    gameContext: GameContext,
    private val vertex: MeshVertexShader,
    private val fragmentShader: UVFragmentShader
) : ImGUIRenderer {

    private val gl = gameContext.gl
    private val verticesBuffer = gl.createBuffer()
    private val verticesOrderBuffer = gl.createBuffer()
    private val verticesUVsBuffer = gl.createBuffer()
    private val textureBuffer = gl.createTexture()
    private val normalsBuffer = gl.createBuffer()

    private val texture: Texture by gameContext.fileHandler.get("internal/widgets.png")

    lateinit var program: ShaderProgram

    override fun render(vertices: FloatArray, uv: FloatArray, verticesOrder: IntArray) {

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

        // Push the texture
        gl.bindTexture(GL.TEXTURE_2D, textureBuffer)

        gl.texParameteri(
            GL.TEXTURE_2D,
            GL.TEXTURE_MAG_FILTER,
            // TODO: this parameter should be configurable at the game level.
            //  Maybe add a config object in the GameContext with fields and an extra as Map
            //  for custom parameters
            GL.NEAREST
        )
        gl.texParameteri(
            GL.TEXTURE_2D,
            GL.TEXTURE_MIN_FILTER,
            GL.NEAREST
        )

        gl.texImage2D(
            GL.TEXTURE_2D,
            0,
            GL.RGBA,
            GL.RGBA,
            GL.UNSIGNED_BYTE,
            texture.textureImage!!
        )

        // ---- shader configuration ---- //

        // Configure the light.

        vertex.uLightColor.apply(program, LightComponent.TRANSPARENT_COLOR)
        vertex.uLightPosition.apply(program, LightComponent.ORIGIN)

        vertex.uModelView.apply(program, Mat4.identity())
        vertex.aVertexPosition.apply(program, verticesBuffer)
        vertex.aVertexNormal.apply(program, normalsBuffer)
        vertex.aUVPosition.apply(program, verticesUVsBuffer)
        fragmentShader.uUV.apply(program, textureBuffer, unit = 0)

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
