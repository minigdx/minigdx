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

    /**
     * Store the local transformation.
     */
    private val localTransformationHolder = TransformationHolder(
        translation,
        rotation,
        scale
    )

    /**
     * Transformation given by the parent transformation and the local transformation.
     */
    var transformation: Mat4 = Mat4.identity()
        get() {
            if (needsToBeUpdated) update()
            return field
        }
        private set

    /**
     * Local transformation. It's relative to the parent transformation
     */
    val localTransformation: Mat4
        get() = this.localTransformationHolder.transformation

    var quaternion: Quaternion = identity()
        get() {
            if (needsToBeUpdated) update()
            return field
        }
        private set

    private val _translation: Vector3 = Vector3()
    val translation: ImmutableVector3 = ImmutableVector3(_translation)
        get() {
            if (needsToBeUpdated) update()
            return field
        }

    private val _localTranslation: Vector3 = Vector3()
    val localTranslation: ImmutableVector3 = ImmutableVector3(_localTranslation)
        get() {
            if (needsToBeUpdated) update()
            return field
        }

    private val _localRotation: Vector3 = Vector3()
    val localRotation: ImmutableVector3 = ImmutableVector3(_localRotation)
        get() {
            if (needsToBeUpdated) update()
            return field
        }

    private val _rotation: Vector3 = Vector3()
    val rotation: ImmutableVector3 = ImmutableVector3(_rotation)
        get() {
            if (needsToBeUpdated) update()
            return field
        }

    val _localScale: Vector3 = Vector3()
    val localScale: ImmutableVector3 = ImmutableVector3(_localScale)
        get() {
            if (needsToBeUpdated) update()
            return field
        }

    val _scale: Vector3 = Vector3(1f, 1f, 1f)
    val scale: ImmutableVector3 = ImmutableVector3(_scale)
        get() {
            if (needsToBeUpdated) update()
            return field
        }

    val localQuaternion: Quaternion
        get() = localTransformationHolder.rotation

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

    fun addGlobalTranslation(translation: Vector3, delta: Seconds = 1f) = addGlobalTranslation(
        translation.x,
        translation.y,
        translation.z,
        delta
    )

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

    fun setLocalTransform(transformation: Mat4): Position {
        localTransformationHolder.transformation = transformation
        return requireUpdate()
    }

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

    fun addLocalRotation(angles: Vector3, delta: Seconds = 1f): Position =
        addLocalRotation(angles.x, angles.y, angles.z, delta)

    fun setLocalRotation(quaternion: Quaternion): Position {
        this.localTransformationHolder.rotation = quaternion
        return requireUpdate()
    }

    fun setLocalRotation(angles: Vector3): Position = setLocalRotation(angles.x, angles.y, angles.z)

    fun setLocalRotation(x: Degree = rotation.x, y: Degree = rotation.y, z: Degree = rotation.z): Position {
        localTransformationHolder.rotation = Quaternion.from(rotation(Float3(x.toFloat(), y.toFloat(), z.toFloat())))
        return requireUpdate()
    }

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

    fun addLocalScale(scale: Vector3, delta: Seconds): Position = addLocalScale(scale.x, scale.y, scale.z, delta)

    fun setLocalScale(x: Percent = localScale.x, y: Percent = localScale.y, z: Percent = localScale.z): Position {
        localTransformationHolder.scale = scale(Float3(x.toFloat(), y.toFloat(), z.toFloat()))
        return requireUpdate()
    }

    fun addWorldScale(x: Percent = 0f, y: Percent = 0f, z: Percent = 0f, delta: Seconds = 1f): Position {
        val parentScale = parentPosition.scale
        localTransformationHolder.scale = scale(
            Float3(
                (parentScale.x + x.toFloat() * delta) / parentScale.x,
                (parentScale.y + y.toFloat() * delta) / parentScale.y,
                (parentScale.z + z.toFloat() * delta) / parentScale.z
            )
        )
        return requireUpdate()
    }

    fun setWorldScale(x: Percent = scale.x, y: Percent = scale.y, z: Percent = scale.z): Position {
        val parent = scale(parentPosition.transformation)
        val scale = scale(
            Float3(
                x.toFloat() / parent.scale.x,
                y.toFloat() / parent.scale.y,
                z.toFloat() / parent.scale.z
            )
        )
        localTransformationHolder.scale = scale
        return requireUpdate()
    }

    fun addWorldRotation(x: Degree = 0, y: Degree = 0, z: Degree = 0, delta: Seconds = 1f): Position {
        return addLocalRotation(x, y, z, delta)
    }

    fun setWorldRotation(x: Degree = 0, y: Degree = 0, z: Degree = 0): Position {
        val parent = rotation(parentPosition.transformation)
        val rotation = rotation(
            Float3(
                x.toFloat() - parent.rotation.x,
                y.toFloat() - parent.rotation.y,
                z.toFloat() - parent.rotation.z
            )
        )
        localTransformationHolder.rotation = Quaternion.from(rotation)
        return requireUpdate()
    }

    fun setWorldRotation(quaternion: Quaternion): Position {
        val rotation = Mat4.from(quaternion).rotation
        return setWorldRotation(rotation.x, rotation.y, rotation.z)
    }

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

        transformation = globalTransformation
        needsToBeUpdated = false
    }

    private fun requireUpdate(): Position {
        needsToBeUpdated = true
        // trigger update
        owner?.componentUpdated(this::class)
        return this
    }

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
