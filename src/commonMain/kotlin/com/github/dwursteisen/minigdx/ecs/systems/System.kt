package com.github.dwursteisen.minigdx.ecs.systems

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.entities.Entity

abstract class System(private val entityQuery: EntityQuery) {

    var entities: List<Entity> = emptyList()

    abstract fun update(delta: Seconds, entity: Entity)

    open fun update(delta: Seconds) {
        entities.forEach { update(delta, it) }
    }

    open fun add(entity: Entity): Boolean {
        return if (entityQuery.accept(entity)) {
            entities = entities + entity
            true
        } else {
            false
        }
    }

    open fun remove(entity: Entity): Boolean {
        return if (entityQuery.accept(entity)) {
            val count = entities.count()
            entities = entities - entity
            return count != entities.count()
        } else {
            false
        }
    }

    fun destroy(): Boolean {
        return entities.map { this.remove(it) }.any { it }
    }
}
