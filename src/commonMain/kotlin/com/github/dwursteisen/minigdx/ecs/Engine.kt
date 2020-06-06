package com.github.dwursteisen.minigdx.ecs

import com.github.dwursteisen.minigdx.Seconds
import kotlin.reflect.KClass

interface Component

class Entity(
    private val engine: Engine,
    components: Collection<Component> = emptyList()
) {
    var components = components
        private set

    var componentsType: Set<KClass<out Component>> = components.map { it::class }.toSet()

    fun add(component: Component) = engineUpdate {
        components += component
        componentsType = componentsType + component::class
    }

    fun remove(component: Component) = engineUpdate {
        components -= component
        componentsType = componentsType - component::class
    }

    fun remove(componentType: KClass<out Component>) = engineUpdate {
        components = components.filter { it::class != componentType }
        componentsType = componentsType - componentType
    }

    private fun engineUpdate(block: () -> Unit) {
        engine.destroy(this)
        block()
        engine.add(this)
    }
}

abstract class System(val entityQuery: EntityQuery) {

    var entities: Sequence<Entity> = emptySequence()

    abstract fun update(delta: Seconds, entity: Entity)

    open fun update(delta: Seconds) = entities.forEach { update(delta, it) }

    open fun add(entity: Entity): Boolean {
        return if (entityQuery.accept(entity)) {
            entities += entity
            true
        } else {
            false
        }
    }

    open fun remove(entity: Entity): Boolean {
        return if (entityQuery.accept(entity)) {
            val count = entities.count()
            entities -= entity
            return count != entities.count()
        } else {
            false
        }
    }
}

class Engine {

    private var systems: Sequence<System> = emptySequence()

    interface EntityBuilder {
        fun add(component: Component)
    }

    class InternalEntityBuilder(private val engine: Engine) : EntityBuilder {

        private var components = emptyList<Component>()

        fun build(): Entity {
            return Entity(engine, components)
        }

        override fun add(component: Component) {
            components = components + component
        }
    }

    fun create(configuration: EntityBuilder.() -> Unit): Entity {
        val builder = InternalEntityBuilder(this)
        builder.configuration()
        return builder.build().also { add(it) }
    }

    fun destroy(entity: Entity): Boolean {
        return systems.any { it.remove(entity) }
    }

    fun addSystem(system: System) {
        systems += system
    }

    fun add(entity: Entity): Boolean {
        return systems.any { it.add(entity) }
    }

    fun update(delta: Seconds) {
        systems.forEach { it.update(delta) }
    }
}
