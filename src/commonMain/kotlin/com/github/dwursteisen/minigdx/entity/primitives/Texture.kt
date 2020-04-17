package com.github.dwursteisen.minigdx.entity.primitives

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.buffer.DataSource
import com.github.dwursteisen.minigdx.entity.CanDraw
import com.github.dwursteisen.minigdx.entity.CanMove
import com.github.dwursteisen.minigdx.entity.delegate.Movable
import com.github.dwursteisen.minigdx.file.TextureImage
import com.github.dwursteisen.minigdx.gl
import com.github.dwursteisen.minigdx.shaders.ShaderProgram

class Texture(source: TextureImage) : CanDraw, CanMove by Movable() {

    private val texture = gl.createTexture()

    private val texCoordBuffer = gl.createBuffer()
    private val positionBuffer = gl.createBuffer()

    init {
        gl.bindTexture(GL.TEXTURE_2D, texture)
        gl.texImage2D(GL.TEXTURE_2D, 0, GL.RGBA, GL.RGBA, GL.UNSIGNED_BYTE, source)
        gl.generateMipmap(GL.TEXTURE_2D)

        // FIXME: quand il y a des modifications sur position, … ça doit être modifié
        gl.bindBuffer(GL.ARRAY_BUFFER, texCoordBuffer)
        gl.bufferData(
            GL.ARRAY_BUFFER, DataSource.FloatDataSource(
                floatArrayOf(
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    0.0f, 1.0f,
                    1.0f, 0.0f,
                    1.0f, 1.0f
                )
            ), GL.STATIC_DRAW
        )

        // FIXME: quand il y a des modifications sur position, … ça doit être modifié
        val x1 = 0f
        val y1 = 0f
        // FIXME: check que les dimensions sont correcte
        val x2 = source.width.toFloat()
        val y2 = source.height.toFloat()

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
    }

    override fun draw(shader: ShaderProgram) {
        gl.enableVertexAttribArray(shader.getAttrib("aPosition"))
        gl.bindBuffer(GL.ARRAY_BUFFER, positionBuffer)
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
