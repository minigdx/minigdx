package com.github.dwursteisen.minigdx.ecs.states

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.events.Event
import com.github.dwursteisen.minigdx.ecs.events.EventWithQuery
import com.github.dwursteisen.minigdx.ecs.events.OnCreate
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.StateMachineSystem
import kotlin.reflect.KClass

typealias Transition<T> = (event: T) -> State?

abstract class State {

    private val eventListeners = mutableMapOf<KClass<out Event>, Transition<Any>>()

    internal val events = mutableListOf<EventWithQuery>()

    open fun configure(entity: Entity) = Unit

    open fun onEnter(entity: Entity) = Unit

    open fun update(delta: Seconds, entity: Entity): State? = null

    open fun onExit(entity: Entity) = Unit

    fun configure(system: StateMachineSystem, entity: Entity) {
        configure(entity)
        system.eventsToListen.addAll(eventListeners.keys)
    }

    fun onCreate(transition: Transition<OnCreate>) = onEvent(
        OnCreate::class,
        transition
    )

    /**
     * Add a transaction to react to a specific event.
     */
    fun <T : Event> onEvent(eventClazz: KClass<T>, transition: Transition<T>) {
        @Suppress("UNCHECKED_CAST")
        eventListeners[eventClazz] = transition as Transition<Any>
    }

    /**
     * React when an event is received by calling the corresponding transition.
     */
    internal fun onEvent(event: Event): State? {
        return eventListeners[event::class]?.invoke(event)
    }

    /**
     * Emit an event. This event can target a group of entities
     */
    fun emitEvents(event: Event, target: EntityQuery? = null) {
        events.add(EventWithQuery.of(event, target))
    }
}
