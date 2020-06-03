package com.github.dwursteisen.minigdx.ecs

import com.github.dwursteisen.minigdx.Seconds
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EngineTest {

    class Name(val name: String) : Component

    class TestSystem : System(EntityQuery(Name::class)) {
        override fun update(delta: Seconds, entity: Entity) = Unit
    }

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

    @Test
    fun `add component | it add the entity in a system when a component is added`() {
        val engine = Engine()
        val system = TestSystem()

        engine.addSystem(system)

        val entity = engine.create {
        }

        entity.add(Name("hello"))
        assertNotNull(system.entities.firstOrNull())
    }

    @Test
    fun `remove component | it remove the entity from a system when a component is removed`() {
        val engine = Engine()
        val system = TestSystem()

        engine.addSystem(system)

        val entity = engine.create {
            add(Name("hello"))
        }

        entity.remove(Name::class)
        assertNull(system.entities.firstOrNull())
    }

    @Test
    fun `update | it update systems`() {
        val engine = Engine()
        var isCalled = false
        val system = object : System(EntityQuery(Name::class)) {

            override fun update(delta: Seconds, entity: Entity) {
                isCalled = true
            }
        }
        engine.addSystem(system)

        engine.create {
            add(Name("hello"))
        }

        engine.update(0.1f)

        assertTrue(isCalled)
    }
}
