package com.github.dwursteisen.minigdx.graphics.compilers

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.ecs.components.gl.GLResourceComponent
import com.github.dwursteisen.minigdx.ecs.components.gl.SpritePrimitive

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
        component.isDirty = false
    }
}
