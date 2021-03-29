package com.github.dwursteisen.minigdx.ecs.components.gl

import kotlin.test.Test
import kotlin.test.assertEquals

class BoundingBoxTest {

    @Test
    fun radius_it_computes_radius() {
        val box = BoundingBox.default()

        assertEquals(1f, box.radius)
    }
}
