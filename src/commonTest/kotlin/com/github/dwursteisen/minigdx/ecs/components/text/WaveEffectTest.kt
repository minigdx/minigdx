package com.github.dwursteisen.minigdx.ecs.components.text

import kotlin.test.Test
import kotlin.test.assertEquals

class WaveEffectTest {

    @Test
    fun waveTextEffect_alterations() {
        val effect = WaveEffect(
            WriteText("test"),
            1f,
            10
        )

        effect.update(1f)
        val a = effect.getAlteration(0)
        val b = effect.getAlteration(1)
        val z = effect.getAlteration(10)

        assertEquals(5, a.y)
        assertEquals(3, b.y)
        assertEquals(-9, z.y)
    }
}
