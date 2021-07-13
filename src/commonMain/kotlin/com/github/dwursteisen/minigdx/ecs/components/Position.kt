package com.github.dwursteisen.minigdx.ecs.components

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.Quaternion
import com.curiouscreature.kotlin.math.Quaternion.Companion.fromEulers
import com.curiouscreature.kotlin.math.Quaternion.Companion.identity
import com.curiouscreature.kotlin.math.interpolate
import com.curiouscreature.kotlin.math.inverse
import com.curiouscreature.kotlin.math.normalize
import com.curiouscreature.kotlin.math.rotation
import com.curiouscreature.kotlin.math.scale
import com.curiouscreature.kotlin.math.translation
import com.github.dwursteisen.minigdx.Coordinate
import com.github.dwursteisen.minigdx.Degree
import com.github.dwursteisen.minigdx.Percent
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.CoordinateConverter.Local
import com.github.dwursteisen.minigdx.ecs.components.position.InternalSimulation
import com.github.dwursteisen.minigdx.ecs.components.position.Simulation
import com.github.dwursteisen.minigdx.ecs.components.position.SimulationResult
import com.github.dwursteisen.minigdx.ecs.components.position.TransformationHolder
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.entities.position
import com.github.dwursteisen.minigdx.math.ImmutableVector3
import com.github.dwursteisen.minigdx.math.Vector3
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

typealias LocalCoordinate = Coordinate
typealias WorldCoordinate = Coordinate

typealias LocalScale = Percent
typealias WorldScale = Percent

sealed class CoordinateConverter {

    abstract fun convert(coordinate: Coordinate, scale: Float): Float

    object Local : CoordinateConverter() {

        override fun convert(coordinate: Coordinate, scale: Float): Float = coordinate.toFloat()
    }

    object World : CoordinateConverter() {

        override fun convert(coordinate: Coordinate, scale: Float): Float {
            return coordinate.toFloat() / scale
        }
    }
}

open class Position(
    translation: Mat4 = Mat4.identity(),
    rotation: Mat4 = Mat4.identity(),
    scale: Mat4 = Mat4.identity()
) : Component {

    private var owner: Entity? = null

    private var needsToBeUpdated: Boolean = true

    inner class UpdateOnRead<T>(val field: KProperty0<T>) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            if (needsToBeUpdated) update()
            return field.get()
        }
    }

    private inline fun <reified T> updateOnRead(field: KProperty0<T>): UpdateOnRead<T> = UpdateOnRead(field)

    /**
     * Store the local transformation.
     */
    private val localTransformationHolder = TransformationHolder(
        translation,
        rotation,
        scale
    )

    private var _transformation: Mat4 = Mat4.identity()
    private var _quaternion: Quaternion = identity()

    private val _translation: Vector3 = Vector3()
    private val _rotation: Vector3 = Vector3()
    private val _scale: Vector3 = Vector3(1f, 1f, 1f)

    private val _localTranslation: Vector3 = Vector3()
    private val _localRotation: Vector3 = Vector3()
    private val _localScale: Vector3 = Vector3()

    private val immutableLocalTranslation = ImmutableVector3(_localTranslation)
    private val immutableLocalRotation = ImmutableVector3(_localRotation)
    private val immutableLocalScale = ImmutableVector3(_localScale)

    private val immutableTranslation = ImmutableVector3(_translation)
    private val immutableRotation = ImmutableVector3(_rotation)
    private val immutableScale = ImmutableVector3(_scale)

    /**
     * Local transformation. It's relative to the parent transformation
     */
    val localTransformation: Mat4
        get() = this.localTransformationHolder.transformation

    /**
     * Local rotation as quaternion
     */
    val localQuaternion: Quaternion
        get() = localTransformationHolder.rotation
    /**
     * Local rotation as degrees.
     */
    val localRotation: ImmutableVector3 by updateOnRead(::immutableLocalRotation)

    /**
     * Local translation.
     */
    val localTranslation: ImmutableVector3 by updateOnRead(::immutableLocalTranslation)

    /**
     * Local scale.
     */
    val localScale: ImmutableVector3 by updateOnRead(::immutableLocalScale)

    /**
     * Transformation given by the parent transformation and the local transformation.
     */
    val transformation: Mat4 by updateOnRead(::_transformation)

    /**
     * Combined rotation as quaternion. ie: parent and local quaternion combined.
     */
    val quaternion: Quaternion by updateOnRead(::_quaternion)

    /**
     * Combined translation.
     */
    val translation: ImmutableVector3 by updateOnRead(::immutableTranslation)

    /**
     * Combined rotation, in degrees
     */
    val rotation: ImmutableVector3 by updateOnRead(::immutableRotation)

    /**
     * Combined scale.
     */
    val scale: ImmutableVector3 by updateOnRead(::immutableScale)

    private val parentPosition: Position
        get() = owner?.parent?.position ?: identity

    init {
        requireUpdate()
    }

    override fun onAdded(entity: Entity) {
        owner = entity
    }

    override fun onRemoved(entity: Entity) {
        owner = null
    }

    override fun onComponentUpdated(componentType: KClass<out Component>) {
        if (componentType == Position::class) {
            needsToBeUpdated = true
        }
    }

    override fun onDetach(parent: Entity) {
        needsToBeUpdated = true
    }

    override fun onAttach(parent: Entity) {
        needsToBeUpdated = true
    }

    // region translation
    /**
     * Add Global translation using World Coordinates
     */
    fun addGlobalTranslation(
        x: WorldCoordinate = 0,
        y: WorldCoordinate = 0,
        z: WorldCoordinate = 0,
        delta: Seconds = 1f
    ): Position {
        val scale = parentPosition.scale
        // Cancel the rotation and scale of the transform
        localTransformationHolder.translation *= translation(
            Float3(
                CoordinateConverter.World.convert(x, scale.x) * delta,
                CoordinateConverter.World.convert(y, scale.y) * delta,
                CoordinateConverter.World.convert(z, scale.z) * delta
            )
        )
        return requireUpdate()
    }

    /**
     * Add Global translation using world coordinate.
     */
    fun addGlobalTranslation(translation: Vector3, delta: Seconds = 1f) = addGlobalTranslation(
        translation.x,
        translation.y,
        translation.z,
        delta
    )

    /**
     * Add Global translation using world coordinate.
     */
    fun addGlobalTranslation(translation: ImmutableVector3, delta: Seconds = 1f) = addGlobalTranslation(
        translation.x,
        translation.y,
        translation.z,
        delta
    )

    /**
     * Add local transaction using the local or world scale,
     * regarding the value of [using].
     *
     */
    fun addLocalTranslation(
        x: LocalCoordinate = 0f,
        y: LocalCoordinate = 0f,
        z: LocalCoordinate = 0f,
        using: CoordinateConverter = Local,
        delta: Seconds = 1f
    ): Position {
        val scale = parentPosition.scale
        val translation = Float3(
            using.convert(x.toFloat(), scale.x) * delta,
            using.convert(y.toFloat(), scale.y) * delta,
            using.convert(z.toFloat(), scale.z) * delta
        )
        localTransformationHolder.translation *= translation(translation)
        return requireUpdate()
    }

    /**
     * Add local transaction using the local or world scale,
     * regarding the value of [using].
     */
    fun addLocalTranslation(
        translation: ImmutableVector3,
        using: CoordinateConverter = Local,
        delta: Seconds = 1f
    ) = addLocalTranslation(
        translation.x,
        translation.y,
        translation.z,
        using,
        delta
    )

    /**
     * Add local transaction using the local or world scale,
     * regarding the value of [using].
     */
    fun addLocalTranslation(
        translation: Vector3,
        using: CoordinateConverter = Local,
        delta: Seconds = 1f
    ) = addLocalTranslation(
        translation.x,
        translation.y,
        translation.z,
        using,
        delta
    )

    /**
     * Set the global translation, in world coordinate.
     */
    fun setGlobalTranslation(
        x: WorldCoordinate = translation.x,
        y: WorldCoordinate = translation.y,
        z: WorldCoordinate = translation.z,
    ): Position {
        val translationInLocalSpace =
            inverse(parentPosition.transformation) *
                translation(
                    Float3(
                        x.toFloat(),
                        y.toFloat(),
                        z.toFloat()
                    )
                )
        localTransformationHolder.translation = translationInLocalSpace
        return requireUpdate()
    }

    /**
     * Set the local translation using the local or world scale,
     * regarding the value of [using].
     */
    fun setLocalTranslation(
        x: Coordinate? = null,
        y: Coordinate? = null,
        z: Coordinate? = null,
        using: CoordinateConverter = Local
    ): Position {
        val scale = parentPosition.scale
        val xScaled = x?.let { using.convert(x, scale.x) } ?: localTranslation.x
        val yScaled = y?.let { using.convert(y, scale.y) } ?: localTranslation.y
        val zScaled = z?.let { using.convert(z, scale.z) } ?: localTranslation.z
        val translation = Float3(
            xScaled,
            yScaled,
            zScaled
        )
        localTransformationHolder.translation = translation(translation)
        return requireUpdate()
    }
    // endregion translation

    /**
     * Set the local transformation.
     */
    fun setLocalTransform(transformation: Mat4): Position {
        localTransformationHolder.transformation = transformation
        return requireUpdate()
    }

    // region rotation
    /**
     * Add local rotation using quaternion.
     */
    fun addLocalRotation(rotation: Quaternion, delta: Seconds = 1f): Position {
        localTransformationHolder.rotation = interpolate(
            localTransformationHolder.rotation,
            normalize(
                Quaternion(
                    localTransformationHolder.rotation.x + rotation.x,
                    localTransformationHolder.rotation.y + rotation.y,
                    localTransformationHolder.rotation.z + rotation.z,
                    localTransformationHolder.rotation.w + rotation.w
                )
            ),
            delta
        )
        return requireUpdate()
    }

    /**
     * Add local rotation using degrees.
     */
    fun addLocalRotation(x: Degree = 0, y: Degree = 0, z: Degree = 0, delta: Seconds = 1f): Position {
        localTransformationHolder.rotation *= fromEulers(
            1f,
            0f,
            0f,
            x.toFloat() * delta
        ) * fromEulers(
            0f,
            1f,
            0f,
            y.toFloat() * delta
        ) * fromEulers(
            0f,
            0f,
            1f,
            z.toFloat() * delta
        )
        return requireUpdate()
    }

    /**
     * Add Local rotation using degrees.
     */
    fun addLocalRotation(angles: Vector3, delta: Seconds = 1f): Position =
        addLocalRotation(angles.x, angles.y, angles.z, delta)

    /**
     * Set local rotation using quaternion.
     */
    fun setLocalRotation(quaternion: Quaternion): Position {
        this.localTransformationHolder.rotation = quaternion
        return requireUpdate()
    }

    /**
     * Set local rotation using degrees.
     */
    fun setLocalRotation(angles: Vector3): Position = setLocalRotation(angles.x, angles.y, angles.z)

    /**
     * Set local rotation using degrees.
     */
    fun setLocalRotation(x: Degree = rotation.x, y: Degree = rotation.y, z: Degree = rotation.z): Position {
        localTransformationHolder.rotation = Quaternion.from(rotation(Float3(x.toFloat(), y.toFloat(), z.toFloat())))
        return requireUpdate()
    }
    // endregion rotation

    // region scale
    /**
     * Add local scale.
     */
    fun addLocalScale(x: Percent = 0f, y: Percent = 0f, z: Percent = 0f, delta: Seconds = 1f): Position {
        localTransformationHolder.scale = scale(
            Float3(
                localScale.x + x.toFloat() * delta,
                localScale.y + y.toFloat() * delta,
                localScale.z + z.toFloat() * delta
            )
        )
        return requireUpdate()
    }

    /**
     * Add local scale.
     */
    fun addLocalScale(scale: Vector3, delta: Seconds): Position = addLocalScale(scale.x, scale.y, scale.z, delta)

    /**
     * Set local scale
     */
    fun setLocalScale(x: Percent = localScale.x, y: Percent = localScale.y, z: Percent = localScale.z): Position {
        localTransformationHolder.scale = scale(Float3(x.toFloat(), y.toFloat(), z.toFloat()))
        return requireUpdate()
    }

    fun setLocalScale(scale: Vector3): Position = setLocalScale(scale.x, scale.y, scale.z)
    // endregion scale

    private fun update() {
        val globalTransformation = parentPosition.transformation * localTransformationHolder.transformation
        val localTranslation = localTransformationHolder.translation.translation

        _localTranslation.set(localTranslation.x, localTranslation.y, localTranslation.z)
        _translation.set(
            globalTransformation.translation.x,
            globalTransformation.translation.y,
            globalTransformation.translation.z
        )

        val localRotation = Mat4.from(localTransformationHolder.rotation)
        _localRotation.set(localRotation.rotation.x, localRotation.rotation.y, localRotation.rotation.z)
        _rotation.set(globalTransformation.rotation.x, globalTransformation.rotation.y, globalTransformation.rotation.z)

        val localScale = localTransformationHolder.scale.scale
        _localScale.set(localScale.x, localScale.y, localScale.z)
        _scale.set(globalTransformation.scale.x, globalTransformation.scale.y, globalTransformation.scale.z)

        _transformation = globalTransformation
        needsToBeUpdated = false
    }

    private fun requireUpdate(): Position {
        needsToBeUpdated = true
        // trigger update
        owner?.componentUpdated(this::class)
        return this
    }

    /**
     * Add local rotation to the position turn around [origin]
     */
    fun addRotationAround(
        origin: Vector3,
        x: Degree = 0,
        y: Degree = 0,
        z: Degree = 0,
        delta: Seconds = 1f
    ): Position {
        val translation = origin.copy().sub(this.translation)

        val translationFromOrigin = translation(translation.toFloat3())
        val rotation = fromEulerAngles(x.toFloat(), y.toFloat(), z.toFloat(), delta)

        localTransformationHolder.translation *= translationFromOrigin *
            Mat4.from(rotation) *
            translation(translation.negate().toFloat3())

        localTransformationHolder.rotation *= rotation
        return requireUpdate()
    }

    /**
     * Simulate a move.
     * At the end of the simulation, the simulation can be rollback (moves are cancelled)
     * or committed (moves are confirmed)
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> simulation(block: Simulation.() -> SimulationResult): T {
        val simulation = InternalSimulation(this)
        val result = block(simulation)
        result.execute(simulation)
        return result.result as T
    }

    companion object {

        val identity = Position().also { it.needsToBeUpdated = false }
    }
}

operator fun Quaternion.times(other: Quaternion): Quaternion = this.mul(other)

private fun fromEulerAngles(x: Float, y: Float, z: Float, delta: Seconds): Quaternion {
    return fromEulers(1f, 0f, 0f, x * delta) *
        fromEulers(0f, 1f, 0f, y * delta) *
        fromEulers(0f, 0f, 1f, z * delta)
}
