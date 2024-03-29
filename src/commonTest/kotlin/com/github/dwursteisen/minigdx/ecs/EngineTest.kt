package com.github.dwursteisen.minigdx.ecs

import ModelFactory.gameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EngineTest {

    class Name(val name: String) : Component

    class TestSystem : System(EntityQuery(Name::class)) {
        override fun update(delta: Seconds, entity: Entity) = Unit
    }

    @Test
    fun create__it_should_add_the_created_entity_into_related_system() {
        val engine = Engine(gameContext())
        val system = TestSystem()

        engine.addSystem(system)

        engine.create {
            add(Name("hello"))
        }

        engine.update(0f)

        assertNotNull(system.entities.firstOrNull())
        assertEquals(1, system.entities.size)
    }

    @Test
    fun remove__it_should_remove_the_entity_from_the_related_system() {
        val engine = Engine(gameContext())
        val system = TestSystem()

        engine.addSystem(system)

        val entity = engine.create {
            add(Name("hello"))
        }

        engine.update(0f)

        val result = engine.destroy(entity)

        assertTrue(result)
        assertNull(system.entities.firstOrNull())
    }

    @Test
    fun add_component__it_add_the_entity_in_a_system_when_a_component_is_added() {
        val engine = Engine(gameContext())
        val system = TestSystem()

        engine.addSystem(system)

        val entity = engine.create {
        }

        entity.add(Name("hello"))

        engine.update(0f)

        assertNotNull(system.entities.firstOrNull())
        assertEquals(1, system.entities.size)
    }

    @Test
    fun remove_component__it_remove_the_entity_from_a_system_when_a_component_is_removed() {
        val engine = Engine(gameContext())
        val system = TestSystem()

        engine.addSystem(system)

        val entity = engine.create {
            add(Name("hello"))
        }

        entity.remove(Name::class)

        engine.update(0f)
        assertNull(system.entities.firstOrNull())
    }

    @Test
    fun update__it_update_systems() {
        val engine = Engine(gameContext())
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

    @Test
    fun destroy__it_destroy_all_entities() {
        val engine = Engine(gameContext())

        var isCalled: Boolean
        val system = object : System(EntityQuery(Name::class)) {

            override fun update(delta: Seconds, entity: Entity) {
                isCalled = true
            }
        }

        engine.addSystem(system)

        engine.create {
            add(Name("hello"))
        }

        engine.update(0f)
        isCalled = false
        engine.destroy()
        engine.update(0f)

        assertFalse(isCalled)
    }
}
