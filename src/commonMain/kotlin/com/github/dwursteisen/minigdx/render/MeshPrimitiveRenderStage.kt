package com.github.dwursteisen.minigdx.render

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.gl.MeshPrimitive
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.graphics.GLResourceClient
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.dwursteisen.minigdx.shaders.fragment.UVFragmentShader
import com.github.dwursteisen.minigdx.shaders.vertex.MeshVertexShader

class MeshPrimitiveRenderStage(gl: GL, compiler: GLResourceClient) : RenderStage<MeshVertexShader, UVFragmentShader>(
    gl = gl,
    compiler = compiler,
    vertex = MeshVertexShader(),
    fragment = UVFragmentShader(),
    query = EntityQuery(MeshPrimitive::class)
) {

    // Distance ; Mesh
    private val transparentPrimitive = mutableListOf<Pair<Position, MeshPrimitive>>()

    private var cameraPosition: Vector3 = Vector3.ZERO

    override fun update(delta: Seconds) {
        cameraPosition = camera?.get(Position::class)?.translation ?: Vector3.ZERO
        super.update(delta)
        gl.enable(GL.BLEND)
        gl.blendFunc(GL.SRC_ALPHA, GL.ONE_MINUS_SRC_ALPHA)
        transparentPrimitive.sortByDescending { (position, _) -> position.translation.dist2(cameraPosition) }
        transparentPrimitive.forEach { (position, primitive) ->
            val model = position.transformation
            vertex.uModelView.apply(program, combinedMatrix * model)
            drawPrimitive(primitive)
        }
        transparentPrimitive.clear()
        gl.disable(GL.BLEND)
    }

    override fun update(delta: Seconds, entity: Entity) {
        val position = entity.get(Position::class)
        val model = position.transformation

        vertex.uModelView.apply(program, combinedMatrix * model)

        entity.findAll(MeshPrimitive::class).forEach { primitive ->
            if (primitive.material.hasAlpha) {
                // defer rendering
                transparentPrimitive.add(position to primitive)
            } else {
                drawPrimitive(primitive)
            }
        }
    }

    private fun drawPrimitive(primitive: MeshPrimitive) {
        if (primitive.isDirty) {
            compiler.compile(primitive)
        }
        vertex.aVertexPosition.apply(program, primitive.verticesBuffer!!)
        vertex.aUVPosition.apply(program, primitive.uvBuffer!!)
        fragment.uUV.apply(program, primitive.textureReference!!, unit = 0)

        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, primitive.verticesOrderBuffer!!)
        gl.drawElements(
            GL.TRIANGLES, primitive.primitive.verticesOrder.size,
            GL.UNSIGNED_SHORT, 0
        )
    }
}
