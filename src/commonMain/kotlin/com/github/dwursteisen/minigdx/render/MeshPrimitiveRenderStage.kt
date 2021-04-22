package com.github.dwursteisen.minigdx.render

import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.inverse
import com.curiouscreature.kotlin.math.rotation
import com.curiouscreature.kotlin.math.translation
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Camera
import com.github.dwursteisen.minigdx.ecs.components.LightComponent
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.UIComponent
import com.github.dwursteisen.minigdx.ecs.components.gl.MeshPrimitive
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.graphics.GLResourceClient
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.dwursteisen.minigdx.math.toVector3
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

    private val cameraDirection: Vector3 = Vector3.FORWARD.copy()
    private val cameraPosition: Vector3 = Vector3.ZERO.copy()

    override fun update(delta: Seconds) {
        camera?.let { cam ->
            val position = cam.get(Position::class)
            val direction = rotation(position.transformation) * translation(Vector3.FORWARD.toFloat3())
            cameraDirection.set(direction.translation.toVector3()).normalize().negate()
            cameraPosition.set(position.transformation.translation.toVector3())
        }

        super.update(delta)
        gl.enable(GL.BLEND)
        gl.blendFunc(GL.SRC_ALPHA, GL.ONE_MINUS_SRC_ALPHA)

        transparentPrimitive.sortByDescending { (position, _) ->
            val translation = position.transformation.translation.toVector3()
            val relativeToCamera = translation.sub(cameraPosition)
            val project = relativeToCamera.project(cameraDirection)
            project.length2()
        }
        transparentPrimitive.forEach { (position, primitive) ->
            val model = position.transformation
            drawPrimitive(primitive, model)
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
                drawPrimitive(primitive, model)
            }
        }
    }

    private fun drawPrimitive(primitive: MeshPrimitive, model: Mat4) {
        if (primitive.isDirty) {
            compiler.compile(primitive)
        }

        // Configure the light.
        val currentLight = light
        if (currentLight == null) {
            // If there is not light, we add a transparent light that should have no effect
            vertex.uLightColor.apply(program, LightComponent.TRANSPARENT_COLOR)
            vertex.uLightPosition.apply(program, LightComponent.ORIGIN)
        } else {
            // We configure the current light
            vertex.uLightColor.apply(program, currentLight.get(LightComponent::class).color)
            // Set the light in the projection space
            val translation = (inverse(model) * currentLight.get(Position::class).transformation).translation
            vertex.uLightPosition.apply(
                program,
                translation.x,
                translation.y,
                translation.z
            )
        }

        vertex.uModelView.apply(program, combinedMatrix * model)
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
