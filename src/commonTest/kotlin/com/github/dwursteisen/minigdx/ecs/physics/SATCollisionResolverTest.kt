package com.github.dwursteisen.minigdx.ecs.physics

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.translation
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SATCollisionResolverTest {

    private val collider = SATCollisionResolver()

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
            translation(Float3(1f, 0f, 0f)),
            square,
            translation(Float3(0.8f, 0f, 0f))
        )
        assertTrue(result)
    }
}
