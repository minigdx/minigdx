package com.github.dwursteisen.minigdx.ecs.physics

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.translation
import com.github.dwursteisen.minigdx.ecs.physics.SATCollisionResolver.Axis
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import com.github.dwursteisen.minigdx.ecs.components.assertEquals as assertEqualsFloat

class SATCollisionResolverTest {

    private val collider = SATCollisionResolver()

    @Test
    fun collide_it_does_not_collide() {
        val result = collider.collide(
            square,
            square
        )
        assertFalse(result)
    }

    @Test
    fun collide_it_collides() {
        val result = collider.collide(
            square,
            square
        )
        assertTrue(result)
    }

    @Test
    fun mightCollide_it_mights_collide() {
        val result = collider.mightCollide(
            square,
            translation(Float3(1f, 0f, 0f)),
            square,
            translation(Float3(1.5f, 0f, 0f))
        )
        assertTrue(result)
    }

    @Test
    fun mightCollide_it_does_not_mights_collide() {
        val result = collider.mightCollide(
            square,
            translation(Float3(1f, 0f, 0f)),
            square,
            translation(Float3(4f, 0f, 0f))
        )
        assertFalse(result)
    }

    @Test
    fun axis_it_extracts_axis_of_triangles() {
        val axis = SATCollisionResolver.Triangle(
            a = Float3(0f, 0f, 0f),
            b = Float3(1f, 0f, 0f),
            c = Float3(0f, 1f, 0f)
        ).axis

        val (a, b, c) = axis
        assertEquals(Axis(0f, 0f, 1f), a)
        assertEquals(0f, b.x)
        assertEqualsFloat(-0.70710677f, b.y)
        assertEqualsFloat(-0.70710677f, b.z)
        assertEquals(Axis(0f, 1f, -0f), c)
    }
}
