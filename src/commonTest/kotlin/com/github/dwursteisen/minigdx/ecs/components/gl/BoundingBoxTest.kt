package com.github.dwursteisen.minigdx.ecs.components.gl

import ModelFactory.vertex
import kotlin.test.Test
import kotlin.test.assertEquals

class BoundingBoxTest {

    @Test
    fun radius_it_computes_radius() {
        val box = BoundingBox(
            vertices = listOf(
                vertex(1f, 0f, 0f),
                vertex(0f, 4f, 0f),
                vertex(0f, 1f, 2f)
            ),
            order = emptyList()
        )

        assertEquals(4f, box.radius)
    }
}
