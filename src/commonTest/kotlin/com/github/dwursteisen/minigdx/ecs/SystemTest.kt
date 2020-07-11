package com.github.dwursteisen.minigdx.ecs

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import kotlin.test.Test
import kotlin.test.assertSame

class SystemTest {

    class ExampleComponent : Component
    class ListeningComponent : Component

    class TestSystem : System(EntityQuery(ExampleComponent::class)) {

        val interested: List<Entity> by interested(EntityQuery(ListeningComponent::class))

        override fun update(delta: Seconds, entity: Entity) {
        }
    }

    @Test
    fun interested_it_returns_list_of_entities() {
        val engine = Engine()
        val system = TestSystem()

        engine.addSystem(system)

        engine.create {
            add(ExampleComponent())
        }

        val expected = engine.create {
            add(ListeningComponent())
        }

        assertSame(expected, system.interested.firstOrNull())
    }
}
