package com.github.dwursteisen.minigdx.render

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
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
