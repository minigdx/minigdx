package com.github.dwursteisen.minigdx.ecs.systems

import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import kotlin.reflect.KClass

open class EntityQuery(
    val include: List<KClass<out Component>> = emptyList(),
    val exclude: List<KClass<out Component>> = emptyList()
) {

    constructor(vararg include: KClass<out Component>) : this(include.toList(), emptyList())

    open fun accept(entity: Entity): Boolean {
        if (exclude.isNotEmpty() && entity.componentsType.containsAll(exclude)) {
            return false
        }
        if (this == all) {
            return true
        }
        if (this == none) {
            return false
        }
        return entity.componentsType.containsAll(include)
    }

    companion object {

        private val all = EntityQuery()
        private val none = EntityQuery()

        /**
         * All entities are refused
         */
        fun none() = none

        /**
         * All entities are accepted
         */
        fun all() = all

        fun of(vararg include: KClass<out Component>) = EntityQuery(include.toList())

        fun of(vararg entities: Entity) = object : EntityQuery() {
            override fun accept(entity: Entity): Boolean {
                return entity in entities
            }
        }
    }
}
