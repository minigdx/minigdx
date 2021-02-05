package com.github.dwursteisen.minigdx.ecs.systems

import ModelFactory.gameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.events.Event
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class EventQueueTest {

    class EmitterComponent : Component
    class ListenerComponent : Component
    class RandomIntEvent(val value: Int = Random.nextInt()) : Event

    class EmitterSystem(val event: RandomIntEvent) : System(EntityQuery(EmitterComponent::class)) {
        override fun update(delta: Seconds, entity: Entity) {
            emit(event)
        }
    }

    class ListenerSystem : System(EntityQuery(ListenerComponent::class)) {

        var eventReceived: Int = -1

        override fun onEvent(event: Event, entityQuery: EntityQuery?) {
            if (event is RandomIntEvent) {
                eventReceived = event.value
            }
        }

        override fun update(delta: Seconds, entity: Entity) = Unit
    }

    @Test
    fun emit_it_reacts_to_emitter_event() {
        val engine = Engine(gameContext())
        val listenerSystem = ListenerSystem()
        val event = RandomIntEvent()

        engine.addSystem(EmitterSystem(event))
        engine.addSystem(listenerSystem)

        engine.create {
            add(EmitterComponent())
            add(ListenerComponent())
        }

        engine.update(0f)

        assertEquals(event.value, listenerSystem.eventReceived)
    }
}
