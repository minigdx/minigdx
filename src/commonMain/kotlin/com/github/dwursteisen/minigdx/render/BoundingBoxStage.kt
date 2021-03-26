package com.github.dwursteisen.minigdx.render

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.position
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.graphics.GLResourceClient
import com.github.dwursteisen.minigdx.shaders.fragment.ColorFragmentShader
import com.github.dwursteisen.minigdx.shaders.vertex.BoundingBoxVertexShader

class BoundingBoxStage(gl: GL, compiler: GLResourceClient) : RenderStage<BoundingBoxVertexShader, ColorFragmentShader>(
    gl = gl,
    compiler = compiler,
    vertex = BoundingBoxVertexShader(),
    fragment = ColorFragmentShader(),
    query = EntityQuery(BoundingBox::class)
) {

    override fun update(delta: Seconds, entity: Entity) {
        val model = entity.get(Position::class).transformation

        val parents = entity.walkOut(combinedMatrix) { acc -> acc * position.transformation }
        vertex.uModelView.apply(program, parents * model)

        val box = entity.get(BoundingBox::class)
        render(box)
    }

    private fun render(box: BoundingBox) {
        if (box.isDirty) {
            compiler.compile(box)
        }
        vertex.aVertexPosition.apply(program, box.verticesBuffer!!)
        vertex.aColor.apply(program, box.colorBuffer!!)
        if (box.touch) {
            vertex.uColor.apply(program, 1f, 0f, 0f, 1f)
        } else {
            vertex.uColor.apply(program, -1f, -1f, -1f, -1f)
        }
        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, box.orderBuffer!!)
        gl.drawElements(
            mask = GL.LINES,
            vertexCount = box.order.size,
            type = GL.UNSIGNED_SHORT,
            offset = 0
        )
    }
}
