package com.github.dwursteisen.minigdx.graphics.compilers

import com.dwursteisen.minigdx.scene.api.common.Id
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.ecs.components.gl.GLResourceComponent
import com.github.dwursteisen.minigdx.ecs.components.gl.MeshPrimitive
import com.github.dwursteisen.minigdx.shaders.DataSource
import com.github.dwursteisen.minigdx.shaders.TextureReference

class MeshPrimitiveCompiler : GLResourceCompiler {

    override fun compile(gl: GL, component: GLResourceComponent, materials: MutableMap<Id, TextureReference>) {
        component as MeshPrimitive

        // Push the model
        component.verticesBuffer = gl.createBuffer()
        gl.bindBuffer(GL.ARRAY_BUFFER, component.verticesBuffer!!)

        gl.bufferData(
            target = GL.ARRAY_BUFFER,
            data = component.primitive.vertices.map { it.position }.positionsDatasource(),
            usage = GL.STATIC_DRAW
        )

        component.verticesOrderBuffer = gl.createBuffer()
        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, component.verticesOrderBuffer!!)
        gl.bufferData(
            target = GL.ELEMENT_ARRAY_BUFFER,
            data = DataSource.ShortDataSource(component.primitive.verticesOrder.map { it.toShort() }
                .toShortArray()),
            usage = GL.STATIC_DRAW
        )

        // Push the texture
        val textureReference = materials.getOrPut(component.material.id) {
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

        component.textureReference = textureReference

        // Push UV coordinates
        component.uvBuffer = gl.createBuffer()
        gl.bindBuffer(GL.ARRAY_BUFFER, component.uvBuffer!!)

        gl.bufferData(
            target = GL.ARRAY_BUFFER,
            data = component.primitive.vertices.map { it.uv }.uvDatasource(),
            usage = GL.STATIC_DRAW
        )
    }

    override fun update(gl: GL, source: GLResourceComponent, target: GLResourceComponent) {
        source as MeshPrimitive
        target as MeshPrimitive

        target.textureReference = source.textureReference
        target.uvBuffer = source.uvBuffer
        target.verticesBuffer = source.verticesBuffer
        target.verticesOrderBuffer = source.verticesOrderBuffer
    }
}
