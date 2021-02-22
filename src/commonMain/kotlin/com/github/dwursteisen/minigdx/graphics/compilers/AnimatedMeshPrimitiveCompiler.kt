package com.github.dwursteisen.minigdx.graphics.compilers

import com.dwursteisen.minigdx.scene.api.common.Id
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.ecs.components.gl.AnimatedMeshPrimitive
import com.github.dwursteisen.minigdx.ecs.components.gl.GLResourceComponent
import com.github.dwursteisen.minigdx.shaders.DataSource
import com.github.dwursteisen.minigdx.shaders.TextureReference

class AnimatedMeshPrimitiveCompiler : GLResourceCompiler {

    override fun compile(gl: GL, component: GLResourceComponent, materials: MutableMap<Id, TextureReference>) {
        component as AnimatedMeshPrimitive
        // Push the model
        component.verticesBuffer = component.verticesBuffer ?: gl.createBuffer()
        gl.bindBuffer(GL.ARRAY_BUFFER, component.verticesBuffer!!)

        gl.bufferData(
            target = GL.ARRAY_BUFFER,
            data = component.primitive.vertices.map { it.position }.positionsDatasource(),
            usage = GL.STATIC_DRAW
        )

        component.verticesOrderBuffer = component.verticesOrderBuffer ?: gl.createBuffer()
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

        component.textureReference = component.textureReference ?: textureReference

        // Push UV coordinates
        component.uvBuffer = component.uvBuffer ?: gl.createBuffer()
        gl.bindBuffer(GL.ARRAY_BUFFER, component.uvBuffer!!)

        gl.bufferData(
            target = GL.ARRAY_BUFFER,
            data = component.primitive.vertices.map { it.uv }.uvDatasource(),
            usage = GL.STATIC_DRAW
        )

        val influences = component.primitive.vertices.flatMap { it.influences }

        component.weightBuffer = component.weightBuffer ?: gl.createBuffer()
        gl.bindBuffer(GL.ARRAY_BUFFER, component.weightBuffer!!)
        gl.bufferData(
            target = GL.ARRAY_BUFFER,
            data = influences.map { it.weight }.weightDatasource(),
            usage = GL.STATIC_DRAW
        )

        component.jointBuffer = component.jointBuffer ?: gl.createBuffer()
        gl.bindBuffer(GL.ARRAY_BUFFER, component.jointBuffer!!)
        gl.bufferData(
            target = GL.ARRAY_BUFFER,
            data = influences.map { it.jointId }.jointDatasource(),
            usage = GL.STATIC_DRAW
        )
    }

    override fun synchronize(gl: GL, source: GLResourceComponent, target: GLResourceComponent, materials: MutableMap<Id, TextureReference>) {
        target as AnimatedMeshPrimitive
        source as AnimatedMeshPrimitive
        target.jointBuffer = source.jointBuffer
        target.verticesBuffer = source.verticesBuffer
        target.weightBuffer = source.weightBuffer
        target.uvBuffer = source.uvBuffer
        target.verticesOrderBuffer = source.verticesOrderBuffer
        target.textureReference = source.textureReference

        if (target.isDirty) {
            compile(gl, target, materials)
        }
    }
}
