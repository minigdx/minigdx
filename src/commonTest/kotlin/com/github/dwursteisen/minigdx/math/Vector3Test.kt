package com.github.dwursteisen.minigdx.math

import com.github.dwursteisen.minigdx.ecs.components.assertEquals
import kotlin.test.Test

class Vector3Test {

    @Test
    fun dist2_it_computes_dist2() {
        val result = Vector3(1, 2, 3).dist2(Vector3(3, 2, 1))
        val a = 3f - 1f
        val b = 2f - 2f
        val c = 1f - 3f
        assertEquals(a * a + b * b + c * c, result)
    }
}
