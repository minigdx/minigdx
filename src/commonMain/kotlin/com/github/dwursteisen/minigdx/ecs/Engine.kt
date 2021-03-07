package com.github.dwursteisen.minigdx.ecs

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.events.EventQueue
import com.github.dwursteisen.minigdx.ecs.systems.System

class Engine(val gameContext: GameContext) {

    private val eventQueue = EventQueue()

    private var systems: List<System> = listOf(eventQueue)

    interface EntityBuilder {
        fun add(component: Component)
        fun add(components: Iterable<Component>)
    }

    private class InternalEntityBuilder(private val engine: Engine) : EntityBuilder {

        private var components = emptyList<Component>()

        fun build(): Entity {
            return Entity(engine, components)
        }

        override fun add(component: Component) {
            components = components + component
        }

        override fun add(components: Iterable<Component>) = components.forEach(::add)
    }

    internal fun onGameStart() {
        systems.forEach {
            it.onGameStart(this)
        }
    }

    fun create(configuration: EntityBuilder.() -> Unit): Entity {
        val builder = InternalEntityBuilder(this)
        builder.configuration()
        return builder.build().also { add(it) }
    }

    fun destroy(entity: Entity): Boolean {
        return systems.map { it.remove(entity) }.any { it }
    }

    fun destroy(): Boolean {
        return systems.map { it.destroy() }.all { it }
    }

    fun addSystem(system: System) {
        val entityQuery = systems.last()
        // Put the entity query always at the end of the list.
        systems = systems.dropLast(1) + system + entityQuery

        // Register the game context and the engine on the system
        system.engine = this
        system.gameContext = gameContext

        eventQueue.register(system)
    }

    fun add(entity: Entity): Boolean {
        return systems
            .map { it.add(entity) to it.consumeEvents() }
            .onEach { (_, events) ->
                events?.run { eventQueue.emit(this) }
            }
            .any { (added, _) -> added }
    }

    fun update(delta: Seconds) {
        systems.forEach {
            it.update(delta)
            it.consumeEvents()?.run { eventQueue.emit(this) }
        }
    }
}
