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

    var componentsType: Set<KClass<out Component>> = components.map { it::class }.toSet()

    fun <T : Component> get(type: KClass<T>): T {
        return componentsByType.getValue(type).toList().first() as T
    }

    fun <T : Component> findAll(type: KClass<T>): List<T> {
        return componentsByType.getValue(type).toList() as List<T>
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

    private fun engineUpdate(block: () -> Unit) {
        engine.destroy(this)
        block()
        engine.add(this)
    }
}
