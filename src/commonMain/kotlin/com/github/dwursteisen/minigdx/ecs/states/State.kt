package com.github.dwursteisen.minigdx.ecs.states

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.events.Event
import com.github.dwursteisen.minigdx.ecs.events.EventWithQuery
import com.github.dwursteisen.minigdx.ecs.events.OnCreate
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.StateMachineSystem
import kotlin.reflect.KClass

typealias Transition = (event: Event) -> State?

abstract class State {

    private val eventListeners = mutableMapOf<KClass<out Event>, Transition>()

    internal val events = mutableListOf<EventWithQuery>()

    open fun configure() = Unit

    open fun onEnter(entity: Entity) = Unit

    open fun update(delta: Seconds, entity: Entity): State? = null

    open fun onExit(entity: Entity) = Unit

    fun configure(system: StateMachineSystem) {
        configure()
        system.eventsToListen.addAll(eventListeners.keys)
    }

    fun onCreate(transition: Transition) = onEvent(
        OnCreate::class, transition
    )

    fun onEvent(eventClazz: KClass<out Event>, transition: Transition) {
        eventListeners[eventClazz] = transition
    }

    fun onEvent(event: Event): State? {
        return eventListeners[event::class]?.invoke(event)
    }

    fun emitEvents(event: Event, target: EntityQuery? = null) {
        events.add(EventWithQuery.of(event, target))
    }
}
