package com.github.dwursteisen.minigdx.render

import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.inverse
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Camera
import com.github.dwursteisen.minigdx.ecs.components.Light
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.UIComponent
import com.github.dwursteisen.minigdx.ecs.components.gl.MeshPrimitive
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.graphics.GLResourceClient
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.dwursteisen.minigdx.shaders.fragment.UVFragmentShader
import com.github.dwursteisen.minigdx.shaders.vertex.MeshVertexShader

class MeshPrimitiveRenderStage(
    gl: GL,
    compiler: GLResourceClient,
    query: EntityQuery = EntityQuery(
        listOf(MeshPrimitive::class),
        listOf(UIComponent::class)
    ),
    cameraQuery: EntityQuery = EntityQuery(
        Camera::class
    )
) : RenderStage<MeshVertexShader, UVFragmentShader>(
    gl = gl,
    compiler = compiler,
    vertex = MeshVertexShader(),
    fragment = UVFragmentShader(),
    query = query,
    cameraQuery = cameraQuery
) {

    // Distance ; Mesh
    private val transparentPrimitive = mutableListOf<Pair<Position, MeshPrimitive>>()

    private var cameraPosition: Vector3 = Vector3.ZERO

    override fun update(delta: Seconds) {
        cameraPosition = camera
            ?.get(Position::class)
            ?.transformation
            ?.let { inverse(it).translation }
            ?.let { Vector3(it.x, it.y, it.z) }
            ?: Vector3.ZERO

        super.update(delta)
        gl.enable(GL.BLEND)
        gl.blendFunc(GL.SRC_ALPHA, GL.ONE_MINUS_SRC_ALPHA)

        // Configure the light.
        val currentLight = light
        if (currentLight == null) {
            // If there is not light, we add a transparent light that should have no effect
            vertex.uLightColor.apply(program, Light.TRANSPARENT_COLOR)
            vertex.uLightPosition.apply(program, Light.ORIGIN)
        } else {
            // We configure the current light
            vertex.uLightColor.apply(program, currentLight.get(Light::class).color)
            vertex.uLightPosition.apply(program, currentLight.get(Position::class).translation)
        }

        transparentPrimitive.sortByDescending { (position, _) -> cameraPosition.dist2(position.translation) }
        transparentPrimitive.forEach { (position, primitive) ->
            val model = position.transformation
            drawPrimitive(primitive, combinedMatrix * model)
        }
        transparentPrimitive.clear()
        gl.disable(GL.BLEND)
    }

    override fun update(delta: Seconds, entity: Entity) {
        val position = entity.get(Position::class)
        val model = position.transformation

        entity.findAll(MeshPrimitive::class).forEach { primitive ->
            if (primitive.hasAlpha) {
                // defer rendering
                transparentPrimitive.add(position to primitive)
            } else {
                drawPrimitive(primitive, combinedMatrix * model)
            }
        }
    }

    private fun drawPrimitive(primitive: MeshPrimitive, modelView: Mat4) {
        if (primitive.isDirty) {
            compiler.compile(primitive)
        }

        when (val trans = primitive.transformation) {
            null -> vertex.uModelView.apply(program, modelView)
            else -> vertex.uModelView.apply(program, modelView * trans)
        }

        vertex.aVertexPosition.apply(program, primitive.verticesBuffer!!)
        vertex.aVertexNormal.apply(program, primitive.normalsBuffer!!)
        vertex.aUVPosition.apply(program, primitive.uvBuffer!!)
        fragment.uUV.apply(program, primitive.textureReference!!, unit = 0)

        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, primitive.verticesOrderBuffer!!)
        gl.drawElements(
            GL.TRIANGLES,
            primitive.primitive.verticesOrder.size,
            GL.UNSIGNED_SHORT,
            0
        )
    }
}
