package com.github.dwursteisen.minigdx.ecs

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Component
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.System

class Engine {

    private var systems: List<System> = emptyList()

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
        systems = systems + system
    }

    fun add(entity: Entity): Boolean {
        return systems.map { it.add(entity) }.any { it }
    }

    fun update(delta: Seconds) {
        systems.forEach { it.update(delta) }
    }
}
