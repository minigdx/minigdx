package com.github.dwursteisen.minigdx.render

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.buffer.DataSource
import com.github.dwursteisen.minigdx.ecs.components.BoundingBox
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.shaders.fragment.ColorFragmentShader
import com.github.dwursteisen.minigdx.shaders.vertex.BoundingBoxVertexShader

class BoundingBoxStage(gl: GL) : RenderStage<BoundingBoxVertexShader, ColorFragmentShader>(
    gl = gl,
    vertex = BoundingBoxVertexShader(),
    fragment = ColorFragmentShader(),
    query = EntityQuery(BoundingBox::class)
) {

    override fun compile(entity: Entity) {
        val boundingBox = entity.get(BoundingBox::class)
        if (boundingBox.verticesBuffer == null) {
            boundingBox.verticesBuffer = gl.createBuffer()
            gl.bindBuffer(GL.ARRAY_BUFFER, boundingBox.verticesBuffer!!)

            gl.bufferData(
                target = GL.ARRAY_BUFFER,
                data = boundingBox.vertices.map { it.position }.positionsDatasource(),
                usage = GL.STATIC_DRAW
            )
        }

        if (boundingBox.orderBuffer == null) {
            boundingBox.orderBuffer = gl.createBuffer()
            gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, boundingBox.orderBuffer!!)
            gl.bufferData(
                target = GL.ELEMENT_ARRAY_BUFFER,
                data = DataSource.ShortDataSource(boundingBox.order.map { it.toShort() }
                    .toShortArray()),
                usage = GL.STATIC_DRAW
            )
        }

        if (boundingBox.colorBuffer == null) {
            boundingBox.colorBuffer = gl.createBuffer()
            gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, boundingBox.colorBuffer!!)
            gl.bufferData(
                target = GL.ELEMENT_ARRAY_BUFFER,
                data = boundingBox.vertices.map { it.color }.colorsDatasource(),
                usage = GL.STATIC_DRAW
            )
        }
    }

    override fun update(delta: Seconds, entity: Entity) {
        val model = entity.get(Position::class).transformation

        vertex.uModelView.apply(program, combinedMatrix * model)

        val box = entity.get(BoundingBox::class)
        vertex.aVertexPosition.apply(program, box.verticesBuffer!!)
        vertex.aColor.apply(program, box.colorBuffer!!)

        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, box.orderBuffer!!)
        gl.drawElements(
            GL.LINES, box.order.size,
            GL.UNSIGNED_SHORT, 0
        )
    }
}
