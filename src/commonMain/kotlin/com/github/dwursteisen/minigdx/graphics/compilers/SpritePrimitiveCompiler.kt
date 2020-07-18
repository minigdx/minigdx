package com.github.dwursteisen.minigdx.graphics.compilers

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.ecs.components.gl.GLResourceComponent
import com.github.dwursteisen.minigdx.ecs.components.gl.SpritePrimitive
import com.github.dwursteisen.minigdx.shaders.DataSource

class SpritePrimitiveCompiler : GLResourceCompiler {

    override fun compile(gl: GL, component: GLResourceComponent) {
        component as SpritePrimitive
        // Push the model
        component.verticesBuffer = gl.createBuffer()
        component.verticesOrderBuffer = gl.createBuffer()
        val textureReference = gl.createTexture()
        gl.bindTexture(GL.TEXTURE_2D, textureReference)
        gl.texParameteri(
            GL.TEXTURE_2D,
            GL.TEXTURE_MAG_FILTER,
            // TODO: this parameter should be configurable at the game level.
            //  Maybe add a config object in the GameContext with fields and an extra as Map
            //  for custom parameters
            GL.LINEAR
        )
        gl.texParameteri(
            GL.TEXTURE_2D,
            GL.TEXTURE_MIN_FILTER,
            GL.LINEAR
        )
        gl.texImage2D(
            GL.TEXTURE_2D,
            0,
            GL.RGBA,
            GL.RGBA,
            GL.UNSIGNED_BYTE,
            component.texture.source
        )

        component.textureReference = textureReference

        component.uvBuffer = gl.createBuffer()

        val vertices = floatArrayOf(
            0f, 0f, -0.1f,
            1f, 0f, -0.1f,
            0f, 1f, -0.1f,
            1f, 1f, -0.1f
        )
        val uv = floatArrayOf(
            0f, 0f,
            1f, 0f,
            0f, 1f,
            1f, 1f
        )
        val order = shortArrayOf(
            1, 3, 2,
            0, 1, 2
        )

        gl.bindBuffer(GL.ARRAY_BUFFER, component.verticesBuffer!!)
        gl.bufferData(
            GL.ARRAY_BUFFER,
            DataSource.FloatDataSource(vertices),
            GL.STATIC_DRAW
        )

        gl.bindBuffer(GL.ARRAY_BUFFER, component.uvBuffer!!)
        gl.bufferData(
            GL.ARRAY_BUFFER,
            DataSource.FloatDataSource(uv),
            GL.STATIC_DRAW
        )

        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, component.verticesOrderBuffer!!)
        gl.bufferData(
            target = GL.ELEMENT_ARRAY_BUFFER, data = DataSource.ShortDataSource(
                order
            ), usage = GL.STATIC_DRAW
        )
        component.numberOfIndices = order.size
        component.isDirty = false
    }
}
