package com.github.dwursteisen.minigdx.ecs.entities

import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Component
import kotlin.reflect.KClass

class Entity(
    private val engine: Engine,
    components: Collection<Component> = emptyList(),
    var name: String = "_",
) {
    var components = components
        private set

    private var componentsByType = components.groupBy { it::class }

    internal var componentsType: Set<KClass<out Component>> = components.map { it::class }.toSet()

    private val _children: MutableList<Entity> = mutableListOf()
    private val _namedChildren: MutableMap<String, Entity> = mutableMapOf()
    val chidren: List<Entity> = _children

    var parent: Entity? = null

    init {
        components.forEach { it.onAdded(this) }
    }

    /**
     * Get a component having the class [type].
     *
     * @return the component
     * @throws NullPointerException if the component doesn't exist on this entity.
     * @see find
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Component> get(type: KClass<T>): T {
        return find(type)!!
    }

    /**
     * Get the first component having the class [type].
     * @return the component or [null] if no component has the class [type].
     * @see get
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Component> find(type: KClass<T>): T? {
        return componentsByType.getValue(type).toList().firstOrNull() as T?
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Component> findAll(type: KClass<T>): List<T> {
        return componentsByType.getValue(type).toList() as List<T>
    }

    fun addAll(components: Collection<Component>) = engineUpdate {
        this.components += components
        componentsType = componentsType + components.map { it::class }.toSet()
        componentsByType = this.components.groupBy { it::class }

        components.forEach { it.onAdded(this) }
    }

    fun add(component: Component) = engineUpdate {
        components += component
        componentsType = componentsType + component::class
        componentsByType = components.groupBy { it::class }

        component.onAdded(this)
    }

    fun remove(component: Component) = engineUpdate {
        components -= component
        componentsType = componentsType - component::class
        componentsByType = components.groupBy { it::class }

        component.onRemoved(this)
    }

    fun remove(componentType: KClass<out Component>) = engineUpdate {
        val removed = components.filter { it::class == componentType }
        components -= removed
        componentsType = componentsType - componentType
        componentsByType = components.groupBy { it::class }
        removed.forEach { it.onRemoved(this) }
    }

    fun destroy(): Boolean {
        _children.forEach { it.destroy() }
        val removed = engine.destroy(this)
        components = emptyList()
        componentsByType = emptyMap()
        componentsType = emptySet()
        return removed
    }

    fun hasComponent(componentClass: KClass<out Component>): Boolean {
        return componentsType.contains(componentClass)
    }

    /**
     * Add current entity as children of the [other] entity.
     */
    fun attachTo(other: Entity?): Entity {
        detach()
        parent = other
        other?._children?.add(this)
        other?._namedChildren?.put(this.name, this)
        parent?.let { p -> this.components.forEach { it.onAttach(p) } }
        return this
    }

    fun detach(): Entity {
        parent?._children?.remove(this)
        parent?._namedChildren?.remove(this.name)
        parent?.let { p -> this.components.forEach { it.onDetach(p) } }
        parent = null
        return this
    }

    fun getChild(name: String): Entity = _namedChildren.get(name)
        ?: throw IllegalArgumentException(
            "Children with name '$name' not found. " +
                "Available children: ${_namedChildren.keys.joinToString(",")}"
        )

    internal fun componentUpdated(componentUpdated: KClass<out Component>) {
        components.forEach { it.onComponentUpdated(componentUpdated) }
        chidren.forEach { it.componentUpdated(componentUpdated) }
    }

    private fun engineUpdate(block: () -> Unit) {
        engine.destroy(this)
        block()
        engine.add(this)
    }

    override fun toString(): String = name
}
