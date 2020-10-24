package com.github.dwursteisen.minigdx.graphics.compilers

import com.dwursteisen.minigdx.scene.api.common.Id
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.ecs.components.gl.GLResourceComponent
import com.github.dwursteisen.minigdx.ecs.components.gl.SpritePrimitive
import com.github.dwursteisen.minigdx.shaders.DataSource
import com.github.dwursteisen.minigdx.shaders.TextureReference

class SpritePrimitiveCompiler : GLResourceCompiler {

    override fun compile(gl: GL, component: GLResourceComponent, materials: MutableMap<Id, TextureReference>) {
        component as SpritePrimitive
        // Push the model
        component.verticesBuffer = component.verticesBuffer ?: gl.createBuffer()
        component.verticesOrderBuffer = component.verticesOrderBuffer ?: gl.createBuffer()

        val textureReference = if (component.texture != null) {
            createByTexture(materials, component, gl)
        } else if (component.material != null) {
            createByMaterial(materials, component, gl)
        } else {
            component.textureReference
        }

        component.textureReference = textureReference

        component.uvBuffer = component.uvBuffer ?: gl.createBuffer()

        val vertices = floatArrayOf(
            0f, 0f, 0f,
            1f, 0f, 0f,
            0f, 1f, 0f,
            1f, 1f, 0f
        )
        val uv = component.uvs

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
    }

    private fun createByMaterial(
        materials: MutableMap<Id, TextureReference>,
        component: SpritePrimitive,
        gl: GL
    ): TextureReference {
        return materials.getOrPut(component.material!!.id) {
            gl.createTexture().apply {
                gl.bindTexture(GL.TEXTURE_2D, this)
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
                    component.material.width,
                    component.material.height,
                    GL.UNSIGNED_BYTE,
                    component.material.data
                )
            }
        }
    }

    private fun createByTexture(
        materials: MutableMap<Id, TextureReference>,
        component: SpritePrimitive,
        gl: GL
    ): TextureReference {
        return materials.getOrPut(component.texture!!.id) {
            gl.createTexture().apply {
                gl.bindTexture(GL.TEXTURE_2D, this)
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
            }
        }
    }

    override fun update(gl: GL, source: GLResourceComponent, target: GLResourceComponent) {
        source as SpritePrimitive
        target as SpritePrimitive

        target.textureReference = source.textureReference
        target.uvBuffer = source.uvBuffer
        target.verticesBuffer = source.verticesBuffer
        target.verticesOrderBuffer = source.verticesOrderBuffer

        gl.bindBuffer(GL.ARRAY_BUFFER, target.uvBuffer!!)
        gl.bufferData(
            GL.ARRAY_BUFFER,
            DataSource.FloatDataSource(target.uvs),
            GL.STATIC_DRAW
        )
    }
}
