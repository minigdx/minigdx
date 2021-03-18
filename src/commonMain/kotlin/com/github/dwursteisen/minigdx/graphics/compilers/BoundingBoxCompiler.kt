package com.github.dwursteisen.minigdx.graphics.compilers

import com.dwursteisen.minigdx.scene.api.common.Id
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.ecs.components.gl.GLResourceComponent
import com.github.dwursteisen.minigdx.shaders.DataSource
import com.github.dwursteisen.minigdx.shaders.TextureReference

class BoundingBoxCompiler : GLResourceCompiler {
    override fun compile(gl: GL, component: GLResourceComponent, materials: MutableMap<Id, TextureReference>) {
        component as BoundingBox
        if (component.verticesBuffer == null) {
            component.verticesBuffer = gl.createBuffer()
            gl.bindBuffer(GL.ARRAY_BUFFER, component.verticesBuffer!!)

            gl.bufferData(
                target = GL.ARRAY_BUFFER,
                data = component.vertices.map { it.position }.positionsDatasource(),
                usage = GL.STATIC_DRAW
            )
        }

        if (component.orderBuffer == null) {
            component.orderBuffer = gl.createBuffer()
            gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, component.orderBuffer!!)
            gl.bufferData(
                target = GL.ELEMENT_ARRAY_BUFFER,
                data = DataSource.ShortDataSource(
                    component.order.map { it.toShort() }
                        .toShortArray()
                ),
                usage = GL.STATIC_DRAW
            )
        }

        if (component.colorBuffer == null) {
            component.colorBuffer = gl.createBuffer()
            gl.bindBuffer(GL.ARRAY_BUFFER, component.colorBuffer!!)
            gl.bufferData(
                target = GL.ARRAY_BUFFER,
                data = component.vertices.map { it.color }.colorsDatasource(),
                usage = GL.STATIC_DRAW
            )
        }
    }

    override fun synchronize(gl: GL, source: GLResourceComponent, target: GLResourceComponent, materials: MutableMap<Id, TextureReference>) {
        source as BoundingBox
        target as BoundingBox
        target.colorBuffer = source.colorBuffer
        target.orderBuffer = source.orderBuffer
        target.verticesBuffer = source.verticesBuffer
    }
}
