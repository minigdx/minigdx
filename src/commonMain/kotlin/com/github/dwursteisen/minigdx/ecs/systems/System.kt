package com.github.dwursteisen.minigdx.ecs.systems

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactory
import com.github.dwursteisen.minigdx.ecs.events.Event
import com.github.dwursteisen.minigdx.ecs.events.EventListener
import com.github.dwursteisen.minigdx.ecs.events.EventWithQuery
import com.github.dwursteisen.minigdx.game.StoryboardEvent
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.logger.Logger
import kotlin.js.JsName
import kotlin.reflect.KProperty

abstract class System(protected val entityQuery: EntityQuery = EntityQuery.none(), gameContext: GameContext? = null) :
    EventListener {

    var entities: List<Entity> = emptyList()

    private var listeners: List<InterestedDelegate> = emptyList()

    private val events = mutableListOf<EventWithQuery>()

    var storyboardEvent: StoryboardEvent? = null
        internal set

    lateinit var entityFactory: EntityFactory

    val logger: Logger by lazy(LazyThreadSafetyMode.NONE) { gameContext?.logger ?: entityFactory.gameContext.logger }

    val input: InputHandler by lazy(LazyThreadSafetyMode.NONE) { gameContext?.input ?: entityFactory.gameContext.input }

    val gameContext: GameContext by lazy(LazyThreadSafetyMode.NONE) { gameContext ?: entityFactory.gameContext }

    class InterestedDelegate(private val query: EntityQuery) {

        val entities: MutableList<Entity> = mutableListOf()

        fun add(entity: Entity) {
            if (query.accept(entity)) {
                entities.add(entity)
            }
        }

        fun remove(entity: Entity) {
            if (query.accept(entity)) {
                entities.remove(entity)
            }
        }

        fun destroy() {
            entities.clear()
        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): List<Entity> {
            return entities
        }
    }

    /**
     * The game started.
     */
    open fun onGameStarted(engine: Engine) = Unit

    /**
     * The [entity] has been successfully added to this system.
     */
    open fun onEntityAdded(entity: Entity) = Unit

    /**
     * The [entity] has been successfully removed from this system.
     */
    open fun onEntityRemoved(entity: Entity) = Unit

    abstract fun update(delta: Seconds, entity: Entity)

    open fun update(delta: Seconds) {
        entities.forEach { update(delta, it) }
    }

    internal open fun add(entity: Entity): Boolean {
        listeners.forEach { it.add(entity) }
        return if (entityQuery.accept(entity)) {
            entities = entities + entity
            onEntityAdded(entity)
            true
        } else {
            false
        }
    }

    internal open fun remove(entity: Entity): Boolean {
        listeners.forEach { it.remove(entity) }
        return if (entityQuery.accept(entity)) {
            val count = entities.count()
            entities = entities - entity
            val wasRemoved = count != entities.count()
            if (wasRemoved) {
                onEntityRemoved(entity)
            }
            return wasRemoved
        } else {
            false
        }
    }

    fun destroy(): Boolean {
        listeners.forEach { it.destroy() }
        return entities.map { this.remove(it) }.any { it }
    }

    /**
     * Override this method to react to received events.
     *
     * Do not call this method!
     */
    override fun onEvent(event: Event, entityQuery: EntityQuery?) = Unit

    /**
     * Emit an event to systems.
     */
    fun emit(event: Event, target: EntityQuery? = null) = emit(EventWithQuery.of(event, target))

    /**
     * Emit a story board event.
     */
    fun emit(storyboardEvent: StoryboardEvent) {
        this.storyboardEvent?.run {
            throw IllegalStateException(
                "A Storyboard event has already been emitted ('${this::class.simpleName}') " +
                    "by the system ${this@System::class.simpleName}. " +
                    "Only one event can be emitted at a time. " +
                    "This new event ('${storyboardEvent::class.simpleName}') should not be emitted."
            )
        }
        this.storyboardEvent = storyboardEvent
    }

    internal fun emit(events: Iterable<EventWithQuery>) {
        this.events.addAll(events)
    }

    internal fun emit(event: EventWithQuery) {
        events.add(event)
    }

    internal fun consumeEvents(): List<EventWithQuery>? {
        return if (events.isEmpty()) {
            null
        } else {
            events.toList().also { events.clear() }
        }
    }

    @JsName("interested")
    fun interested(query: EntityQuery): InterestedDelegate {
        val delegate = InterestedDelegate(query)
        listeners = listeners + delegate
        return delegate
    }
}
