package com.github.dwursteisen.minigdx.ecs.systems

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TemporalSystemTest {

    class TestSystem(var called: Boolean = false) : TemporalSystem(1f) {

        override fun update(delta: Seconds, entity: Entity) = Unit

        override fun timeElapsed() {
            called = true
        }
    }
    @Test
    fun timeElapsed_it_calls_the_method_after_the_elapsed_time() {
        val system = TestSystem()
        system.update(0.5f)
        assertFalse(system.called)
        system.update(0.6f)
        assertTrue(system.called)
    }
}
