package com.github.dwursteisen.minigdx.ecs.systems

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import kotlin.reflect.KClass

interface Event

class OnCreate : Event

typealias Transition = (event: Event) -> State?

abstract class StateMachineComponent(var state: State? = null) : Component

interface EventListener {

    fun onEvent(event: Event, entity: Entity)
}

abstract class State {

    private val eventListeners = mutableMapOf<KClass<out Event>, Transition>()

    abstract fun configure()

    open fun onEnter() = Unit

    abstract fun update(delta: Seconds, entity: Entity): State?

    open fun onExit() = Unit

    fun configure(system: StateMachineSystem) {
        configure()
        system.eventListeners.putAll(eventListeners)
    }

    fun onCreate(transition: Transition) = onEvent(OnCreate::class, transition)

    fun onEvent(eventClazz: KClass<out Event>, transition: Transition) {
        eventListeners[eventClazz] = transition
    }
}

abstract class StateMachineSystem(private val stateMachineComponent: KClass<out StateMachineComponent>) : System(EntityQuery(stateMachineComponent)), EventListener {

    internal val eventListeners = mutableMapOf<KClass<out Event>, Transition>()

    abstract fun initialState(entity: Entity): State

    override fun add(entity: Entity): Boolean {
        val isAdded = super.add(entity)
        if (isAdded) {
            entity.newState(initialState(entity))
        }
        return isAdded
    }

    override fun update(delta: Seconds, entity: Entity) {
        val newState = entity.get(stateMachineComponent).state?.update(delta, entity)
        entity.newState(newState)
    }

    override fun onEvent(event: Event, entity: Entity) {
        val transition = eventListeners.get(event::class)
        transition?.run {
            val newState = transition(event)
            entity.newState(newState)
        }
    }

    private fun Entity.newState(newState: State?) {
        newState ?: return
        val component = get(stateMachineComponent)
        component.state?.onExit()
        component.state = newState
        component.state?.configure(this@StateMachineSystem)
        component.state?.onEnter()
    }
}
