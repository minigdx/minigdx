package com.github.dwursteisen.minigdx.render

import com.dwursteisen.minigdx.scene.api.common.Id
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.BoundingBoxComponent
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.file.Texture
import com.github.dwursteisen.minigdx.graph.Model
import com.github.dwursteisen.minigdx.graph.Primitive
import com.github.dwursteisen.minigdx.shaders.fragment.ColorFragmentShader
import com.github.dwursteisen.minigdx.shaders.vertex.BoundingBoxVertexShader

class BoundingBoxRenderStage(gameContext: GameContext) : RenderStage<BoundingBoxVertexShader, ColorFragmentShader>(
    gameContext = gameContext,
    vertex = BoundingBoxVertexShader(),
    fragment = ColorFragmentShader(),
    query = EntityQuery.of(BoundingBoxComponent::class)
) {

    private val cube = Model(
        primitives = listOf(
            Primitive(
                texture = Texture(Id(), byteArrayOf(), 0, 0, false),
                vertices = floatArrayOf(
                    -1f, -1f, -1f,
                    1f, -1f, -1f,
                    -1f, 1f, -1f,
                    1f, 1f, -1f,
                    -1f, -1f, 1f,
                    1f, -1f, 1f,
                    -1f, 1f, 1f,
                    1f, 1f, 1f
                ),
                normals = floatArrayOf(),
                uvs = floatArrayOf(),
                verticesOrder = shortArrayOf(
                    // front
                    0, 1,
                    1, 3,
                    3, 2,
                    2, 0,
                    // back
                    4, 5,
                    5, 7,
                    7, 6,
                    6, 4,
                    // sides
                    4, 0,
                    1, 5,
                    2, 6,
                    3, 7
                )
            )
        )
    )

    override fun onGameStarted(engine: Engine) {
        this.gameContext.assetsManager.add(cube)
    }

    override fun update(delta: Seconds, entity: Entity) {
        val model = entity.get(Position::class).transformation

        val box = entity.get(BoundingBoxComponent::class)

        vertex.uModelView.apply(program, combinedMatrix * model * box.fromDefaultTransformation)

        render(box)
        box.touch = false
    }

    private fun render(box: BoundingBoxComponent) = cube.primitives.forEach { primitive ->
        vertex.aVertexPosition.apply(program, primitive.verticesBuffer!!)
        if (box.touch) {
            vertex.uColor.apply(program, 1f, 0f, 0f, 1f)
        } else {
            vertex.uColor.apply(program, -1f, -1f, -1f, -1f)
        }
        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, primitive.verticesOrderBuffer!!)
        gl.drawElements(
            mask = GL.LINES,
            vertexCount = primitive.verticesOrder.size,
            type = GL.UNSIGNED_SHORT,
            offset = 0
        )
    }
}
