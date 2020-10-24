package com.github.dwursteisen.minigdx.ecs.systems

import com.github.dwursteisen.minigdx.MiniGdx
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.StateMachineComponent
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.events.Event
import com.github.dwursteisen.minigdx.ecs.states.State
import com.github.dwursteisen.minigdx.logger.Logger
import kotlin.reflect.KClass

abstract class StateMachineSystem(
    private val stateMachineComponent: KClass<out StateMachineComponent>,
    private val logger: Logger
) : System(EntityQuery(stateMachineComponent)) {

    internal val eventsToListen = mutableSetOf<KClass<out Event>>()

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

    override fun onEvent(event: Event, entityQuery: EntityQuery?) {
        if (!eventsToListen.contains(event::class)) {
            return
        }

        entities.forEach { entity ->
            if (entityQuery == null || entityQuery.accept(entity)) {
                val newState = entity.get(stateMachineComponent).state?.onEvent(event)
                entity.newState(newState)
            }
        }
    }

    private fun Entity.newState(newState: State?) {
        val component = get(stateMachineComponent)
        if (newState != null) {
            component.state?.onExit(this)
            consumeEvents(component.state)

            if (MiniGdx.debugStates) {
                logger.info("STATES") {
                    "[${this@StateMachineSystem::class}] (${component.state?.let { it::class } ?: "??"}) --> (${newState::class})"
                }
            }

            component.state = newState
            component.state?.configure(this@StateMachineSystem)
            component.state?.onEnter(this)
            consumeEvents(component.state)
        } else {
            consumeEvents(component.state)
        }
    }

    private fun consumeEvents(state: State?) = state?.run {
        emit(events)
        events.clear()
    }
}
