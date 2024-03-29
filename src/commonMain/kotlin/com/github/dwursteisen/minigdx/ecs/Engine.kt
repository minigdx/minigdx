package com.github.dwursteisen.minigdx.ecs

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactoryDelegate
import com.github.dwursteisen.minigdx.ecs.events.EventQueue
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.game.StoryboardEvent

class Engine(val gameContext: GameContext) {

    private val eventQueue = EventQueue()

    private var systems: List<System> = listOf(eventQueue)

    private val waitingForUpdate = mutableListOf<() -> Unit>()
    private val executingForUpdate = mutableListOf<() -> Unit>()

    val entityFactory = EntityFactoryDelegate().let { factory ->
        factory.gameContext = gameContext
        factory.engine = this
        factory
    }

    interface EntityBuilder {

        fun named(name: String)
        fun add(component: Component)
        fun add(components: Iterable<Component>)
    }

    private class InternalEntityBuilder(private val engine: Engine) : EntityBuilder {

        private var components = emptyList<Component>()

        private var name: String = "_"

        fun build(): Entity {
            return Entity(engine, components, name)
        }

        override fun named(name: String) {
            this.name = name
        }

        override fun add(component: Component) {
            components = components + component
        }

        override fun add(components: Iterable<Component>) = components.forEach { add(it) }
    }

    internal fun onGameStart() {
        waitingForUpdate.forEach { action -> action() }
        waitingForUpdate.clear()
        systems.forEach {
            it.onGameStarted(this)
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
        system.entityFactory = entityFactory

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

    internal fun queueEntityUpdate(action: () -> Unit) {
        waitingForUpdate.add(action)
    }

    fun update(delta: Seconds) {
        // Copy all actions and prepare the execution
        executingForUpdate.addAll(waitingForUpdate)
        // The execution might trigger new updates.
        // So the queue is cleaned before the execution
        waitingForUpdate.clear()
        // If there is new updates, those updates will
        // end into the 'waitingForUpdate' queue.
        executingForUpdate.forEach { action -> action() }
        // The execution succeed. Let's clean up the rest!
        executingForUpdate.clear()

        var storyboardEvent: StoryboardEvent? = null
        systems.forEach {
            it.update(delta)
            it.consumeEvents()?.run { eventQueue.emit(this) }

            val newStoryboardEvent = it.storyboardEvent
            it.storyboardEvent = null
            if (newStoryboardEvent != null && storyboardEvent != null) {
                throw IllegalStateException(
                    "A Storyboard event has already been emitted ('${storyboardEvent!!::class.simpleName}')." +
                        "Only one event can be emitted at a time. " +
                        "This new event ('${newStoryboardEvent::class.simpleName}') should not be emitted."
                )
            } else if (newStoryboardEvent != null) {
                storyboardEvent = newStoryboardEvent
            }
        }

        gameContext.storyboardEvent = storyboardEvent
    }
}
