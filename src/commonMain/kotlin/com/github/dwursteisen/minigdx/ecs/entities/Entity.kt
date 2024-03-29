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

    private val _inFlightComponents = mutableListOf<Component>()

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
        return find(type) ?: throw IllegalStateException(
            "No components of type '${type.simpleName}' " +
                "found in the entity '${this.name}'. The entity contains those components: " +
                (componentsType.map { it.simpleName } + _inFlightComponents.map { it::class.simpleName })
                    .ifEmpty { listOf("None") }
                    .joinToString()
        )
    }

    /**
     * Get the first component having the class [type].
     * @return the component or [null] if no component has the class [type].
     * @see get
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Component> find(type: KClass<T>): T? {
        return componentsByType[type]
            ?.toList()
            ?.firstOrNull() as T?
            ?: getFromInFlight(type).firstOrNull() as T?
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Component> findAll(type: KClass<T>): List<T> {
        val components = componentsByType[type]
            ?.toList()
            ?: getFromInFlight(type)
        return components as List<T>
    }

    /**
     * Add all components to the entity
     */
    fun addAll(components: Collection<Component>): Entity {
        _inFlightComponents.addAll(components)
        return engineUpdate {
            _inFlightComponents.removeAll(components)
            this.components += components
            componentsType = componentsType + components.map { it::class }.toSet()
            componentsByType = this.components.groupBy { it::class }

            components.forEach { it.onAdded(this) }
        }
    }

    /**
     * All the component to this entity
     */
    fun add(component: Component): Entity {
        _inFlightComponents.add(component)
        return engineUpdate {
            _inFlightComponents.remove(component)
            components += component
            componentsType = componentsType + component::class
            componentsByType = components.groupBy { it::class }

            component.onAdded(this)
        }
    }

    /**
     * Remove the component to the entity.
     */
    fun remove(component: Component) = engineUpdate {
        components -= component
        componentsType = componentsType - component::class
        componentsByType = components.groupBy { it::class }

        component.onRemoved(this)
    }

    /**
     * Remove components of this type of the entity.
     */
    fun remove(componentType: KClass<out Component>) = engineUpdate {
        val removed = components.filter { it::class == componentType }
        components -= removed
        componentsType = componentsType - componentType
        componentsByType = components.groupBy { it::class }
        removed.forEach { it.onRemoved(this) }
    }

    /**
     * Destroy the entity.
     */
    fun destroy(): Boolean {
        _children.forEach { it.destroy() }
        val removed = engine.destroy(this)
        components = emptyList()
        componentsByType = emptyMap()
        componentsType = emptySet()
        return removed
    }

    fun hasComponent(componentClass: KClass<out Component>): Boolean {
        val contains = componentsType.contains(componentClass)
        if (!contains) {
            return getFromInFlight(componentClass).isNotEmpty()
        }
        return contains
    }

    /**
     * Add current entity as children of the [other] entity.
     */
    fun attachTo(other: Entity?): Entity {
        if (other == this) {
            throw IllegalArgumentException("The entity ${this.name} can't be attached to itself.")
        }
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

    private fun engineUpdate(block: () -> Unit): Entity {
        // Queue the action so it will be executed during the next render loop.
        engine.queueEntityUpdate {
            engine.destroy(this)
            block()
            engine.add(this)
        }
        return this
    }

    private fun getFromInFlight(type: KClass<out Component>): Collection<Component> {
        return _inFlightComponents.filter { component -> type.isInstance(component) }
    }

    override fun toString(): String = name
}
