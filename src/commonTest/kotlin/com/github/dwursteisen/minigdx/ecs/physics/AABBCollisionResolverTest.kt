package com.github.dwursteisen.minigdx.ecs.physics

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.translation
import com.dwursteisen.minigdx.scene.api.model.Normal
import com.dwursteisen.minigdx.scene.api.model.Position
import com.dwursteisen.minigdx.scene.api.model.Vertex
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AABBCollisionResolverTest {

    private val collider = AABBCollisionResolver()

    private val square = BoundingBox(
        vertices = listOf(
            Vertex(
                position = Position(0f, 0f, 0f),
                normal = Normal(0f, 0f, 0f)
            ),
            Vertex(
                position = Position(1f, 0f, 0f),
                normal = Normal(0f, 0f, 0f)
            ),
            Vertex(
                position = Position(1f, 1f, 0f),
                normal = Normal(0f, 0f, 0f)
            ),
            Vertex(
                position = Position(0f, 1f, 0f),
                normal = Normal(0f, 0f, 0f)
            ),
            Vertex(
                position = Position(0f, 0f, 1f),
                normal = Normal(0f, 0f, 0f)
            ),
            Vertex(
                position = Position(1f, 0f, 1f),
                normal = Normal(0f, 0f, 0f)
            ),
            Vertex(
                position = Position(1f, 1f, 1f),
                normal = Normal(0f, 0f, 0f)
            ),
            Vertex(
                position = Position(0f, 1f, 1f),
                normal = Normal(0f, 0f, 0f)
            )

        ),
        order = emptyList()
    )

    @Test
    fun collide_it_does_not_collide() {
        val result = collider.collide(
            square,
            translation(Float3(0f, 0f, 0f)),
            square,
            translation(Float3(2f, 0f, 0f))
        )
        assertFalse(result)
    }

    @Test
    fun collide_it_collides() {
        val result = collider.collide(
            square,
            translation(Float3(0.8f, 0f, 0f)),
            square,
            translation(Float3(1f, 0f, 0f))
        )
        assertTrue(result)
    }
}
