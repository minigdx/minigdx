package com.github.dwursteisen.minigdx.render

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.AnimatedModel
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.gl.AnimatedMeshPrimitive
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.shaders.fragment.UVFragmentShader
import com.github.dwursteisen.minigdx.shaders.vertex.AnimatedMeshVertexShader

class AnimatedMeshPrimitiveRenderStage(gl: GL) : RenderStage<AnimatedMeshVertexShader, UVFragmentShader>(
    gl = gl,
    vertex = AnimatedMeshVertexShader(),
    fragment = UVFragmentShader(),
    query = EntityQuery(AnimatedMeshPrimitive::class)
) {

    override fun update(delta: Seconds, entity: Entity) {
        val model = entity.get(Position::class).transformation
        val animatedModel = entity.get(AnimatedModel::class)

        vertex.uModelView.apply(program, combinedMatrix * model)
        vertex.uJointTransformationMatrix.apply(program, animatedModel.currentPose)

        entity.findAll(AnimatedMeshPrimitive::class).forEach { primitive ->
            vertex.aVertexPosition.apply(program, primitive.verticesBuffer!!)
            vertex.aUVPosition.apply(program, primitive.uvBuffer!!)
            vertex.aWeights.apply(program, primitive.weightBuffer!!)
            vertex.aJoints.apply(program, primitive.jointBuffer!!)
            fragment.uUV.apply(program, primitive.textureReference!!, unit = 0)

            gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, primitive.verticesOrderBuffer!!)
            gl.drawElements(
                GL.TRIANGLES, primitive.primitive.verticesOrder.size,
                GL.UNSIGNED_SHORT, 0
            )
        }
    }
}
