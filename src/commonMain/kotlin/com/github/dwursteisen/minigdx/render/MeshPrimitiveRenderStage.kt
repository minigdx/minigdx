package com.github.dwursteisen.minigdx.render

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.gl.MeshPrimitive
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.shaders.fragment.UVFragmentShader
import com.github.dwursteisen.minigdx.shaders.vertex.MeshVertexShader

class MeshPrimitiveRenderStage(gl: GL) : RenderStage<MeshVertexShader, UVFragmentShader>(
    gl = gl,
    vertex = MeshVertexShader(),
    fragment = UVFragmentShader(),
    query = EntityQuery(MeshPrimitive::class)
) {

    override fun update(delta: Seconds, entity: Entity) {
        val model = entity.get(Position::class).transformation

        vertex.uModelView.apply(program, combinedMatrix * model)

        entity.findAll(MeshPrimitive::class).forEach { primitive ->
            vertex.aVertexPosition.apply(program, primitive.verticesBuffer!!)
            vertex.aUVPosition.apply(program, primitive.uvBuffer!!)
            fragment.uUV.apply(program, primitive.textureReference!!, unit = 0)

            gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, primitive.verticesOrderBuffer!!)
            gl.drawElements(
                GL.TRIANGLES, primitive.primitive.verticesOrder.size,
                GL.UNSIGNED_SHORT, 0)
        }
    }
}
