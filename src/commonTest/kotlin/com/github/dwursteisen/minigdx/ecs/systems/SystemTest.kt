package com.github.dwursteisen.minigdx.ecs.systems

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import createGlContext
import kotlin.test.Test
import kotlin.test.assertSame

class SystemTest {

    class ExampleComponent : Component
    class ListeningComponent : Component

    class TestSystem : System(EntityQuery(ExampleComponent::class)) {

        val interestedBy: List<Entity> by interested(EntityQuery(ListeningComponent::class))

        override fun update(delta: Seconds, entity: Entity) {
        }
    }

    @Test
    fun interested_it_returns_list_of_entities() {
        val engine = Engine(GameContext(createGlContext()))
        val system = TestSystem()

        engine.addSystem(system)

        engine.create {
            add(ExampleComponent())
        }

        val expected = engine.create {
            add(ListeningComponent())
        }

        assertSame(expected, system.interestedBy.firstOrNull())
    }
}
