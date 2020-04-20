package com.github.dwursteisen.minigdx.entity.primitives

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.buffer.DataSource
import com.github.dwursteisen.minigdx.entity.CanDraw
import com.github.dwursteisen.minigdx.entity.CanMove
import com.github.dwursteisen.minigdx.entity.delegate.Movable
import com.github.dwursteisen.minigdx.file.TextureImage
import com.github.dwursteisen.minigdx.gl
import com.github.dwursteisen.minigdx.shaders.ShaderProgram

class Texture(private val source: TextureImage) : CanDraw, CanMove by Movable() {

    private val texture = gl.createTexture()

    private val texCoordBuffer = gl.createBuffer()
    private val positionBuffer = gl.createBuffer()

    init {
        gl.bindTexture(GL.TEXTURE_2D, texture)
        gl.texImage2D(GL.TEXTURE_2D, 0, GL.RGBA, GL.RGBA, GL.UNSIGNED_BYTE, source)
        gl.generateMipmap(GL.TEXTURE_2D)
    }

    override fun draw(shader: ShaderProgram) {
        draw(
            shader = shader,
            x = position.x,
            y = position.y,
            width = source.width.toFloat(),
            height = source.height.toFloat(),
            textureX = 0,
            textureY = 0,
            textureWidth = source.width,
            textureHeight = source.height
        )
    }

    fun draw(
        shader: ShaderProgram,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        textureX: Int,
        textureY: Int,
        textureWidth: Int,
        textureHeight: Int
    ) {
        gl.blendFunc(GL.SRC_ALPHA, GL.ONE_MINUS_SRC_ALPHA)
        gl.enable(GL.BLEND)

        gl.bindTexture(GL.TEXTURE_2D, texture)

        //   https://webglfundamentals.org/webgl/lessons/fr/webgl-2d-rotation.html
        gl.enableVertexAttribArray(shader.getAttrib("aPosition"))

        val x1 = x
        val y1 = y

        val x2 = x + width * scale.x
        val y2 = y + height * scale.y

        gl.bindBuffer(GL.ARRAY_BUFFER, positionBuffer)
        gl.bufferData(GL.ARRAY_BUFFER, DataSource.FloatDataSource(
            floatArrayOf(
                x1, y1,
                x2, y1,
                x1, y2,
                x1, y2,
                x2, y1,
                x2, y2
            )
        ), GL.STATIC_DRAW)

        gl.vertexAttribPointer(
            index = shader.getAttrib("aPosition"),
            size = 2,
            type = GL.FLOAT,
            normalized = false,
            stride = 0,
            offset = 0
        )

        gl.enableVertexAttribArray(shader.getAttrib("aTexCoord"))
        gl.bindBuffer(GL.ARRAY_BUFFER, texCoordBuffer)

        val tx = textureX / source.width.toFloat()
        val ty = textureY / source.height.toFloat()
        val tw = textureWidth / source.width.toFloat()
        val th = textureHeight / source.height.toFloat()
        gl.bufferData(
            GL.ARRAY_BUFFER, DataSource.FloatDataSource(
                floatArrayOf(
                    tx, ty,
                    tx + tw, ty,
                    tx, ty + th,
                    tx, ty + th,
                    tx + tw, ty,
                    tx + tw, ty + th
                )
            ), GL.STATIC_DRAW
        )

        gl.vertexAttribPointer(
            index = shader.getAttrib("aTexCoord"),
            size = 2,
            type = GL.FLOAT,
            normalized = false,
            stride = 0,
            offset = 0
        )

        gl.uniform2f(
            shader.getUniform("uResolution"),
            gl.screen.width.toFloat(),
            gl.screen.height.toFloat()
        )

        // Draw the rectangle.
        val primitiveType = GL.TRIANGLES
        val offset = 0
        val count = 6
        gl.drawArrays(primitiveType, offset, count)
    }
}
