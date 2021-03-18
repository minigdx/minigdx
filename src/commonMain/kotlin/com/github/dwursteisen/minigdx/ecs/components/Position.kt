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
import com.github.dwursteisen.minigdx.math.Vector3

class TransformationHolder(
    translation: Mat4 = Mat4.identity(),
    rotation: Mat4 = Mat4.identity(),
    scale: Mat4 = Mat4.identity()
) {

    private var combined: Mat4 = Mat4.identity()

    var transalation: Mat4 = translation(translation)
        set(value) {
            field = translation(value)
            combined = updateTransformation()
        }

    var rotation: Quaternion = normalize(Quaternion.from(rotation(rotation)))
        set(value) {
            field = value
            combined = updateTransformation()
        }

    var scale: Mat4 = scale(scale)
        set(value) {
            field = scale(value)
            combined = updateTransformation()
        }

    var transformation: Mat4
        get() = combined
        set(value) {
            transalation = translation(value)
            rotation = normalize(
                Quaternion.from(
                    rotation(
                        Float3(
                            value.rotation.x,
                            value.rotation.y,
                            value.rotation.z
                        )
                    )
                )
            )
            scale = scale(value)
            combined = updateTransformation()
        }

    init {
        combined = updateTransformation()
    }

    private fun updateTransformation(): Mat4 {
        return transalation * Mat4.from(rotation) * scale
    }
}

class Position(
    globalTranslation: Mat4 = Mat4.identity(),
    globalRotation: Mat4 = Mat4.identity(),
    globalScale: Mat4 = Mat4.identity()
) : Component {

    /**
     * Store the global transformation.
     *
     */
    private val globalTransformation = TransformationHolder(globalTranslation, globalRotation, globalScale)

    /**
     * Store the local transformation.
     */
    private val localTransformation = TransformationHolder()

    /**
     * Transformation given by the global transformation and then the local transformation.
     */
    var transformation: Mat4 = this.globalTransformation.transformation * localTransformation.transformation
        private set

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
        get() = localTransformation.rotation
    val globalQuaternion: Quaternion
        get() = globalTransformation.rotation

    init {
        update()
    }

    fun setLocalTransform(transformation: Mat4): Position {
        localTransformation.transformation = transformation
        return update()
    }

    fun setGlobalTransform(transformation: Mat4): Position {
        globalTransformation.transformation = transformation
        return update()
    }

    fun setGlobalRotation(
        x: Degree = globalRotation.x,
        y: Degree = globalRotation.y,
        z: Degree = globalRotation.z
    ): Position {
        globalTransformation.rotation = normalize(
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
        globalTransformation.rotation *= fromEulers(1f, 0f, 0f, x.toFloat() * delta) *
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
        localTransformation.rotation *= fromEulers(
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
        this.quaternion = quaternion
        return update()
    }

    fun setLocalRotation(angles: Vector3): Position = setLocalRotation(angles.x, angles.y, angles.z)

    fun setLocalRotation(x: Degree = rotation.x, y: Degree = rotation.y, z: Degree = rotation.z): Position {
        localTransformation.rotation = Quaternion.from(rotation(Float3(x.toFloat(), y.toFloat(), z.toFloat())))
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
        localTransformation.scale = scale(Float3(x.toFloat(), y.toFloat(), z.toFloat()))
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
        globalTransformation.scale = scale(Float3(x.toFloat(), y.toFloat(), z.toFloat()))
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
        globalTransformation.transalation = translation(Float3(x.toFloat(), y.toFloat(), z.toFloat()))
        return update()
    }

    fun addGlobalTranslation(
        x: Coordinate = 0,
        y: Coordinate = 0,
        z: Coordinate = 0,
        delta: Seconds = 1f
    ): Position {
        globalTransformation.transalation *= translation(
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
        localTransformation.transalation = translation(Float3(x.toFloat(), y.toFloat(), z.toFloat()))
        return update()
    }

    fun addLocalTranslation(x: Coordinate = 0, y: Coordinate = 0, z: Coordinate = 0, delta: Seconds = 1f): Position {
        localTransformation.transalation *= translation(
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
        transformation = globalTransformation.transformation * localTransformation.transformation
        // Translation
        val lt = localTransformation.transalation.translation
        localTranslation.set(lt.x, lt.y, lt.z)
        val gt = globalTransformation.transalation.translation
        globalTranslation.set(gt.x, gt.y, gt.z)
        val ct = transformation.translation
        translation.set(ct.x, ct.y, ct.z)

        // Rotation
        val lr = Mat4.from(localTransformation.rotation).rotation
        localRotation.set(lr.x, lr.y, lr.z)
        val gr = Mat4.from(globalTransformation.rotation).rotation
        globalRotation.set(gr.x, gr.y, gr.z)
        val cr = transformation.rotation
        rotation.set(cr.x, cr.y, cr.z)

        quaternion = Quaternion.from(transformation)

        // Scale
        val ls = localTransformation.scale.scale
        localScale.set(ls.x, ls.y, ls.z)
        val gs = globalTransformation.scale.scale
        globalScale.set(gs.x, gs.y, gs.z)
        val cs = transformation.scale
        scale.set(cs.x, cs.y, cs.z)

        return this
    }

    fun addGlobalRotationAround(
        origin: Vector3,
        x: Degree = 0,
        y: Degree = 0,
        z: Degree = 0,
        delta: Seconds = 1f
    ): Position {
        val translation = this.globalTranslation.copy().sub(origin)
        val translationFromOrigin = translation(Float3(translation.x, translation.y, translation.z))

        val quaternion1 = fromEulerAngles(x.toFloat(), y.toFloat(), z.toFloat(), delta)
        val pivot = translation(Float3(origin.x, origin.y, origin.z)) * Mat4.from(quaternion1)

        globalTransformation.transalation = pivot * translationFromOrigin
        globalTransformation.rotation *= quaternion1
        return update()
    }
}

operator fun Quaternion.times(other: Quaternion): Quaternion = this.mul(other)

private fun fromEulerAngles(x: Float, y: Float, z: Float, delta: Seconds): Quaternion {
    return fromEulers(1f, 0f, 0f, x * delta) *
        fromEulers(0f, 1f, 0f, y * delta) *
        fromEulers(0f, 0f, 1f, z * delta)
}
