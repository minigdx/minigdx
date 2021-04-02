package com.github.dwursteisen.minigdx.ecs.components

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.Quaternion
import com.curiouscreature.kotlin.math.Quaternion.Companion.fromEulers
import com.curiouscreature.kotlin.math.interpolate
import com.curiouscreature.kotlin.math.normalize
import com.curiouscreature.kotlin.math.rotation
import com.curiouscreature.kotlin.math.scale
import com.curiouscreature.kotlin.math.translation
import com.github.dwursteisen.minigdx.Coordinate
import com.github.dwursteisen.minigdx.Degree
import com.github.dwursteisen.minigdx.Percent
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.position.InternalSimulation
import com.github.dwursteisen.minigdx.ecs.components.position.Simulation
import com.github.dwursteisen.minigdx.ecs.components.position.SimulationResult
import com.github.dwursteisen.minigdx.ecs.components.position.TransformationHolder
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.math.Vector3
import kotlin.reflect.KClass

open class Position(
    globalTranslation: Mat4 = Mat4.identity(),
    globalRotation: Mat4 = Mat4.identity(),
    globalScale: Mat4 = Mat4.identity()
) : Component {

    private var owner: Entity? = null

    private var needsToBeUpdated: Boolean = true

    /**
     * Store the global transformation.
     *
     */
    private val globalTransformationHolder = TransformationHolder(globalTranslation, globalRotation, globalScale)

    /**
     * Store the local transformation.
     */
    private val localTransformationHolder = TransformationHolder()

    /**
     * Transformation given by the global transformation and then the local transformation.
     */
    var transformation: Mat4 = this.globalTransformationHolder.transformation * localTransformationHolder.transformation
        private set

    val globalTransformation: Mat4
        get() = this.globalTransformationHolder.transformation

    val localTransformation: Mat4
        get() = this.localTransformationHolder.transformation

    var quaternion = normalize(Quaternion.from(transformation))
        private set

    val globalTranslation: Vector3 = Vector3()
    val localTranslation: Vector3 = Vector3()
    val translation: Vector3 = Vector3()

    val globalRotation: Vector3 = Vector3()
    val localRotation: Vector3 = Vector3()
    val rotation: Vector3 = Vector3()

    val globalScale: Vector3 = Vector3()
    val localScale: Vector3 = Vector3()
    val scale: Vector3 = Vector3()

    val localQuaternion: Quaternion
        get() = localTransformationHolder.rotation
    val globalQuaternion: Quaternion
        get() = globalTransformationHolder.rotation

    private var _combinedTransformation: Mat4 = Mat4.identity()

    val combinedTransformation: Mat4
        get() {
            if (needsToBeUpdated) {
                val parentTransformation = owner?.parent?.get(Position::class)?.combinedTransformation ?: Mat4.identity()
                _combinedTransformation = parentTransformation * transformation
                needsToBeUpdated = false
            }
            return _combinedTransformation
        }

    init {
        update()
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

    fun setLocalTransform(transformation: Mat4): Position {
        localTransformationHolder.transformation = transformation
        return update()
    }

    fun setGlobalTransform(transformation: Mat4): Position {
        globalTransformationHolder.transformation = transformation
        return update()
    }

    fun setGlobalRotation(quaternion: Quaternion): Position {
        globalTransformationHolder.rotation = quaternion
        return update()
    }

    fun setGlobalRotation(
        x: Degree = globalRotation.x,
        y: Degree = globalRotation.y,
        z: Degree = globalRotation.z
    ): Position {
        globalTransformationHolder.rotation = normalize(
            Quaternion.from(
                rotation(
                    Float3(
                        x.toFloat(),
                        y.toFloat(),
                        z.toFloat()
                    )
                )
            )
        )
        return update()
    }

    fun addGlobalRotation(
        x: Degree = 0f,
        y: Degree = 0f,
        z: Degree = 0f,
        delta: Seconds = 1f
    ): Position {
        globalTransformationHolder.rotation *= fromEulers(1f, 0f, 0f, x.toFloat() * delta) *
            fromEulers(0f, 1f, 0f, y.toFloat() * delta) *
            fromEulers(0f, 0f, 1f, z.toFloat() * delta)
        return update()
    }

    fun addLocalRotation(rotation: Quaternion, delta: Seconds = 1f): Position {
        quaternion = interpolate(
            quaternion,
            normalize(
                Quaternion(
                    quaternion.x + rotation.x,
                    quaternion.y + rotation.y,
                    quaternion.z + rotation.z,
                    quaternion.w + rotation.w
                )
            ),
            delta
        )
        return update()
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
        return update()
    }

    fun addLocalRotation(angles: Vector3, delta: Seconds = 1f): Position =
        addLocalRotation(angles.x, angles.y, angles.z, delta)

    fun setLocalRotation(quaternion: Quaternion): Position {
        this.localTransformationHolder.rotation = quaternion
        return update()
    }

    fun setLocalRotation(angles: Vector3): Position = setLocalRotation(angles.x, angles.y, angles.z)

    fun setLocalRotation(x: Degree = rotation.x, y: Degree = rotation.y, z: Degree = rotation.z): Position {
        localTransformationHolder.rotation = Quaternion.from(rotation(Float3(x.toFloat(), y.toFloat(), z.toFloat())))
        return update()
    }

    fun addLocalScale(x: Percent = 0f, y: Percent = 0f, z: Percent = 0f, delta: Seconds = 1f): Position {
        return setLocalScale(
            localScale.x + x.toFloat() * delta,
            localScale.y + y.toFloat() * delta,
            localScale.z + z.toFloat() * delta
        )
    }

    fun addLocalScale(scale: Vector3, delta: Seconds): Position = addLocalScale(scale.x, scale.y, scale.z, delta)

    fun setLocalScale(x: Percent = localScale.x, y: Percent = localScale.y, z: Percent = localScale.z): Position {
        localTransformationHolder.scale = scale(Float3(x.toFloat(), y.toFloat(), z.toFloat()))
        return update()
    }

    fun addGlobalScale(x: Percent = 0f, y: Percent = 0f, z: Percent = 0f, delta: Seconds = 1f): Position {
        return setGlobalScale(
            globalScale.x + x.toFloat() * delta,
            globalScale.y + y.toFloat() * delta,
            globalScale.z + z.toFloat() * delta
        )
    }

    fun setGlobalScale(x: Percent = globalScale.x, y: Percent = globalScale.y, z: Percent = globalScale.z): Position {
        globalTransformationHolder.scale = scale(Float3(x.toFloat(), y.toFloat(), z.toFloat()))
        return update()
    }

    fun setGlobalTranslation(
        position: Vector3
    ) = setGlobalTranslation(position.x, position.y, position.z)

    fun setGlobalTranslation(
        x: Coordinate = globalTranslation.x,
        y: Coordinate = globalTranslation.y,
        z: Coordinate = globalTranslation.z
    ): Position {
        globalTransformationHolder.transalation = translation(Float3(x.toFloat(), y.toFloat(), z.toFloat()))
        return update()
    }

    fun addGlobalTranslation(
        x: Coordinate = 0,
        y: Coordinate = 0,
        z: Coordinate = 0,
        delta: Seconds = 1f
    ): Position {
        globalTransformationHolder.transalation *= translation(
            Float3(
                x.toFloat() * delta,
                y.toFloat() * delta,
                z.toFloat() * delta
            )
        )
        return update()
    }

    fun setLocalTranslation(
        x: Coordinate = localTranslation.x,
        y: Coordinate = localTranslation.y,
        z: Coordinate = localTranslation.z
    ): Position {
        localTransformationHolder.transalation = translation(Float3(x.toFloat(), y.toFloat(), z.toFloat()))
        return update()
    }

    fun addLocalTranslation(x: Coordinate = 0, y: Coordinate = 0, z: Coordinate = 0, delta: Seconds = 1f): Position {
        localTransformationHolder.transalation *= translation(
            Float3(
                x.toFloat() * delta,
                y.toFloat() * delta,
                z.toFloat() * delta
            )
        )
        return update()
    }

    fun addImmediateLocalTranslation(x: Coordinate = 0, y: Coordinate = 0, z: Coordinate = 0) =
        addLocalTranslation(x, y, z, 1f)

    private fun update(): Position {
        transformation = globalTransformationHolder.transformation * localTransformationHolder.transformation
        // Translation
        val lt = localTransformationHolder.transalation.translation
        localTranslation.set(lt.x, lt.y, lt.z)
        val gt = globalTransformationHolder.transalation.translation
        globalTranslation.set(gt.x, gt.y, gt.z)
        val ct = transformation.translation
        translation.set(ct.x, ct.y, ct.z)

        // Rotation
        val lr = Mat4.from(localTransformationHolder.rotation).rotation
        localRotation.set(lr.x, lr.y, lr.z)
        val gr = Mat4.from(globalTransformationHolder.rotation).rotation
        globalRotation.set(gr.x, gr.y, gr.z)
        val cr = transformation.rotation
        rotation.set(cr.x, cr.y, cr.z)

        quaternion = Quaternion.from(transformation)

        // Scale
        val ls = localTransformationHolder.scale.scale
        localScale.set(ls.x, ls.y, ls.z)
        val gs = globalTransformationHolder.scale.scale
        globalScale.set(gs.x, gs.y, gs.z)
        val cs = transformation.scale
        scale.set(cs.x, cs.y, cs.z)

        // trigger update
        owner?.componentUpdated(this::class)
        return this
    }

    fun addGlobalRotationAround(
        origin: Vector3,
        x: Degree = 0,
        y: Degree = 0,
        z: Degree = 0,
        delta: Seconds = 1f
    ): Position {
        val translation = origin.copy().sub(this.globalTranslation)

        val translationFromOrigin = translation(translation.toFloat3())
        val rotation = fromEulerAngles(x.toFloat(), y.toFloat(), z.toFloat(), delta)

        globalTransformationHolder.transalation *= translationFromOrigin *
            Mat4.from(rotation) *
            translation(translation.negate().toFloat3())

        globalTransformationHolder.rotation *= rotation
        return update()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> simulation(block: Simulation.() -> SimulationResult): T {
        val simulation = InternalSimulation(this)
        val result = block(simulation)
        result.execute(simulation)
        return result.result as T
    }
}

operator fun Quaternion.times(other: Quaternion): Quaternion = this.mul(other)

private fun fromEulerAngles(x: Float, y: Float, z: Float, delta: Seconds): Quaternion {
    return fromEulers(1f, 0f, 0f, x * delta) *
        fromEulers(0f, 1f, 0f, y * delta) *
        fromEulers(0f, 0f, 1f, z * delta)
}
