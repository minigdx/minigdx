package com.github.dwursteisen.minigdx.ecs

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.rotation
import com.github.dwursteisen.minigdx.Degree
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.buffer.Buffer
import com.github.dwursteisen.minigdx.gl
import com.github.dwursteisen.minigdx.math.Vector3
import kotlin.reflect.KClass

interface Component

abstract class BufferComponent(var buffer: Buffer? = null) : Component
class WithUV(buffer: Buffer? = null) : BufferComponent(buffer)
class WithColor(buffer: Buffer? = null) : BufferComponent(buffer)

data class Position(
    var transformation: Mat4 = Mat4.identity(),
    val translation: Vector3 = transformation.position.let { Vector3(it.x, it.y, it.z) },
    val rotation: Vector3 = transformation.rotation.let { Vector3(it.x, it.y, it.z) },
    val scale: Vector3 = transformation.scale.let { Vector3(it.x, it.y, it.z) },
    var way: Float = 1f
) : Component {
    fun rotate(x: Degree = 0, y: Degree = 0, z: Degree = 0): Position {
        rotateX(x.toFloat())
        rotateY(y.toFloat())
        rotateZ(z.toFloat())
        return this
    }

    fun rotate(angles: Vector3): Position = rotate(angles.x, angles.y, angles.z)

    fun rotateX(angle: Degree): Position {
        val asFloat = angle.toFloat() * way
        rotation.x += asFloat
        transformation *= rotation(
            Float3(
                1f,
                0f,
                0f
            ), asFloat
        )
        return this
    }

    fun rotateY(angle: Degree): Position {
        val asFloat = angle.toFloat() * way
        rotation.y += asFloat
        transformation *= rotation(
            Float3(
                0f,
                1f,
                0f
            ), asFloat
        )
        return this
    }

    fun rotateZ(angle: Degree): Position {
        val asFloat = angle.toFloat() * way
        rotation.z += asFloat
        transformation *= rotation(
            Float3(
                0f,
                0f,
                1f
            ), asFloat
        )
        return this
    }
/*
    fun setRotation(quaternion: Quaternion): Position
    fun setRotation(angles: Vector3): Position = setRotationX(angles.x)
        .setRotationY(angles.y)
        .setRotationZ(angles.z)
    fun setRotationX(angle: Degree): Position
    fun setRotationY(angle: Degree): Position
    fun setRotationZ(angle: Degree): Position

    fun translate(x: Coordinate = 0f, y: Coordinate = 0f, z: Coordinate = 0f): Position
    fun translate(move: Vector3): CanMove = translate(move.x, move.y, move.z)

    fun setTranslate(x: Coordinate = translation.x, y: Coordinate = translation.y, z: Coordinate = translation.z): Position
    fun setTranslate(move: Vector3): Position = setTranslate(move.x, move.y, move.z)

    fun scale(x: Percent = scale.x, y: Percent = scale.y, z: Percent = scale.z): Position
    fun scale(scale: Vector3): Position = scale(scale.x, scale.y, scale.z)

    fun setScale(x: Percent = 1, y: Percent = 1, z: Percent = 1): Position
    fun setScale(scale: Vector3): Position = setScale(scale.x, scale.y, scale.z)

 */
}

class Entity(
    private val engine: Engine,
    components: Collection<Component> = emptyList()
) {
    var components = components
        private set

    private var componentsByType = components.groupBy { it::class }

    var componentsType: Set<KClass<out Component>> = components.map { it::class }.toSet()

    operator fun <T : Component> get(type: KClass<T>): List<T> {
        return componentsByType.getValue(type) as List<T>
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

abstract class System(private val entityQuery: EntityQuery) {

    var entities: List<Entity> = emptyList()

    abstract fun update(delta: Seconds, entity: Entity)

    open fun update(delta: Seconds) {
        // FIXME: remove me
        gl.clearColor(0f, 0f, 0f, 1f)
        gl.clearDepth(1.0)
        gl.enable(GL.DEPTH_TEST)
        gl.depthFunc(GL.LEQUAL)
        gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)

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

class Engine {

    private var systems: List<System> = emptyList()

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
