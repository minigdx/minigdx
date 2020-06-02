package com.github.dwursteisen.minigdx.ecs

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EngineTest {

    class Name(val name: String) : Component

    class TestSystem : System(EntityQuery(Name::class))

    @Test
    fun `create | it should add the created entity into related system`() {
        val engine = Engine()
        val system = TestSystem()

        engine.addSystem(system)

        engine.create {
            add(Name("hello"))
        }

        assertNotNull(system.entities.firstOrNull())
    }

    @Test
    fun `remove | it should remove the entity from the related system`() {
        val engine = Engine()
        val system = TestSystem()

        engine.addSystem(system)

        val entity = engine.create {
            add(Name("hello"))
        }

        val result = engine.destroy(entity)

        assertTrue(result)
        assertNull(system.entities.firstOrNull())
    }
}
