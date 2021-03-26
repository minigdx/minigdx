package com.github.dwursteisen.minigdx.ecs.entities

import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Component
import kotlin.reflect.KClass

class Entity(
    private val engine: Engine,
    components: Collection<Component> = emptyList()
) {
    var components = components
        private set

    private var componentsByType = components.groupBy { it::class }

    internal var componentsType: Set<KClass<out Component>> = components.map { it::class }.toSet()

    private val _childrens: MutableList<Entity> = mutableListOf()
    val chidrens: List<Entity> = _childrens

    var parent: Entity? = null

    @Suppress("UNCHECKED_CAST")
    fun <T : Component> get(type: KClass<T>): T {
        return componentsByType.getValue(type).toList().first() as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Component> findAll(type: KClass<T>): List<T> {
        return componentsByType.getValue(type).toList() as List<T>
    }

    fun addAll(components: Collection<Component>) = engineUpdate {
        this.components += components
        componentsType = componentsType + components.map { it::class }.toSet()
        componentsByType = this.components.groupBy { it::class }
    }

    fun add(component: Component) = engineUpdate {
        components += component
        componentsType = componentsType + component::class
        componentsByType = components.groupBy { it::class }
    }

    fun remove(component: Component) = engineUpdate {
        components -= component
        componentsType = componentsType - component::class
        componentsByType = components.groupBy { it::class }
    }

    fun remove(componentType: KClass<out Component>) = engineUpdate {
        components = components.filter { it::class != componentType }
        componentsType = componentsType - componentType
        componentsByType = components.groupBy { it::class }
    }

    fun destroy() = engine.destroy(this)

    fun hasComponent(componentClass: KClass<out Component>): Boolean {
        return componentsType.contains(componentClass)
    }

    /**
     * Add current entity as children of the [other] entity.
     */
    fun attachTo(other: Entity?): Entity {
        detach()
        parent = other
        other?._childrens?.add(this)
        return this
    }

    fun detach(): Entity {
        parent?._childrens?.remove(this)
        parent = null
        return this
    }

    fun <T> walkOut(initialValue: T, executionBlock: Entity.(accumulator: T) -> T): T {
        return parent?.executionBlock(initialValue) ?: initialValue
    }

    private fun engineUpdate(block: () -> Unit) {
        engine.destroy(this)
        block()
        engine.add(this)
    }
}
