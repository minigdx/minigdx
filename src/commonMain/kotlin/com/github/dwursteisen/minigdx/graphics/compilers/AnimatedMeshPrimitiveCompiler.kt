package com.github.dwursteisen.minigdx.graphics.compilers

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.buffer.DataSource
import com.github.dwursteisen.minigdx.ecs.components.gl.AnimatedMeshPrimitive
import com.github.dwursteisen.minigdx.ecs.components.gl.GLResourceComponent

class AnimatedMeshPrimitiveCompiler : GLResourceCompiler {

    override fun compile(gl: GL, component: GLResourceComponent) {
        component as AnimatedMeshPrimitive
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
        val textureReference = gl.createTexture()
        gl.bindTexture(GL.TEXTURE_2D, textureReference)

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

        component.textureReference = textureReference

        // Push UV coordinates
        component.uvBuffer = gl.createBuffer()
        gl.bindBuffer(GL.ARRAY_BUFFER, component.uvBuffer!!)

        gl.bufferData(
            target = GL.ARRAY_BUFFER,
            data = component.primitive.vertices.map { it.uv }.uvDatasource(),
            usage = GL.STATIC_DRAW
        )

        component.weightBuffer = gl.createBuffer()
        gl.bindBuffer(GL.ARRAY_BUFFER, component.weightBuffer!!)
        gl.bufferData(
            target = GL.ARRAY_BUFFER,
            data = component.primitive.vertices.flatMap { it.influences }.map { it.weight }.weightDatasource(),
            usage = GL.STATIC_DRAW
        )

        component.jointBuffer = gl.createBuffer()
        gl.bindBuffer(GL.ARRAY_BUFFER, component.jointBuffer!!)
        gl.bufferData(
            target = GL.ARRAY_BUFFER,
            data = component.primitive.vertices.flatMap { it.influences }.map { it.jointId }.jointDatasource(),
            usage = GL.STATIC_DRAW
        )

        component.isCompiled = true
        component.isDirty = false
    }
}
