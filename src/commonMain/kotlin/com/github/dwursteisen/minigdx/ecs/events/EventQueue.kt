package com.github.dwursteisen.minigdx.ecs.events

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System

class EventQueue : System(EntityQuery()) {

    private val listeners = mutableListOf<EventListener>()

    override fun update(delta: Seconds, entity: Entity) = Unit

    override fun update(delta: Seconds) {
        consumeEvents()?.forEach { eventWrapper ->
            listeners.forEach { listener ->
                listener.onEvent(eventWrapper.event, eventWrapper.entityQuery)
            }
        }
    }

    fun register(listener: EventListener) {
        listeners.add(listener)
    }
}
