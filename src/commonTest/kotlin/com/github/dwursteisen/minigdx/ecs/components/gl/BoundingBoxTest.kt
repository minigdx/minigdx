package com.github.dwursteisen.minigdx.ecs.components.gl

import com.github.dwursteisen.minigdx.ecs.components.assertEquals
import kotlin.test.Test

class BoundingBoxTest {

    @Test
    fun radius_it_computes_radius() {
        val box = BoundingBox.default()

        assertEquals(1.7320508f, box.radius, 0.001f)
    }
}
