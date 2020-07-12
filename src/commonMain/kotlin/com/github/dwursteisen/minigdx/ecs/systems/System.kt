package com.github.dwursteisen.minigdx.ecs.systems

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import kotlin.js.JsName
import kotlin.reflect.KProperty

abstract class System(private val entityQuery: EntityQuery) {

    var entities: List<Entity> = emptyList()

    var listeners: List<InterestedDelegate> = emptyList()

    abstract fun update(delta: Seconds, entity: Entity)

    open fun update(delta: Seconds) {
        entities.forEach { update(delta, it) }
    }

    open fun add(entity: Entity): Boolean {
        listeners.forEach { it.add(entity) }
        return if (entityQuery.accept(entity)) {
            entities = entities + entity
            true
        } else {
            false
        }
    }

    open fun remove(entity: Entity): Boolean {
        listeners.forEach { it.remove(entity) }
        return if (entityQuery.accept(entity)) {
            val count = entities.count()
            entities = entities - entity
            return count != entities.count()
        } else {
            false
        }
    }

    fun destroy(): Boolean {
        listeners.forEach { it.destroy() }
        return entities.map { this.remove(it) }.any { it }
    }

    @JsName("interested")
    fun interested(query: EntityQuery): InterestedDelegate {
        val delegate = InterestedDelegate(query)
        listeners = listeners + delegate
        return delegate
    }
}

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
