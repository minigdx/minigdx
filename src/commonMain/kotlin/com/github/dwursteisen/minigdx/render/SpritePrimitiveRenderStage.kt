package com.github.dwursteisen.minigdx.render

import com.curiouscreature.kotlin.math.Mat4
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.UICamera
import com.github.dwursteisen.minigdx.ecs.components.gl.SpritePrimitive
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.shaders.fragment.UVFragmentShader
import com.github.dwursteisen.minigdx.shaders.vertex.MeshVertexShader

class SpritePrimitiveRenderStage(gl: GL) : RenderStage<MeshVertexShader, UVFragmentShader>(
    gl = gl,
    vertex = MeshVertexShader(),
    fragment = UVFragmentShader(),
    query = EntityQuery(SpritePrimitive::class),
    cameraQuery = EntityQuery(UICamera::class)
) {

    override val combinedMatrix: Mat4
        get() {
            return camera?.let {
                val view = it.get(Position::class).transformation
                val projection = it.get(UICamera::class).projection
                projection * view
            } ?: Mat4.identity()
        }

    override fun update(delta: Seconds, entity: Entity) {
        val model = entity.get(Position::class).transformation
        val primitive = entity.get(SpritePrimitive::class)

        vertex.uModelView.apply(program, combinedMatrix * model)

        vertex.aVertexPosition.apply(program, primitive.verticesBuffer!!)
        vertex.aUVPosition.apply(program, primitive.uvBuffer!!)
        fragment.uUV.apply(program, primitive.textureReference!!, unit = 0)

        entity.get(SpritePrimitive::class).renderStrategy.render(gl, entity)
    }
}
