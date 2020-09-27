package com.github.dwursteisen.minigdx.ecs.components

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.Quaternion
import com.curiouscreature.kotlin.math.TWO_PI
import com.curiouscreature.kotlin.math.interpolate
import com.curiouscreature.kotlin.math.normalize
import com.curiouscreature.kotlin.math.radians
import com.curiouscreature.kotlin.math.scale
import com.curiouscreature.kotlin.math.translation
import com.github.dwursteisen.minigdx.Coordinate
import com.github.dwursteisen.minigdx.Degree
import com.github.dwursteisen.minigdx.Percent
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.math.Vector3
import kotlin.math.cos
import kotlin.math.sin

data class Position(
    var transformation: Mat4 = Mat4.identity(),
    private var translationMatrix: Mat4 = translation(transformation.translation),
    private var scaleMatrix: Mat4 = scale(transformation.scale),
    var way: Float = 1f
) : Component {

    private var quaternion = normalize(Quaternion.from(transformation))

    val translation: Vector3 = Vector3()
    val rotation: Vector3 = Vector3()
    val scale: Vector3 = Vector3()

    fun addLocalRotation(rotation: Quaternion, delta: Seconds): Position {
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
        return updateMatrix()
    }

    fun addLocalRotation(x: Degree = 0, y: Degree = 0, z: Degree = 0, delta: Seconds): Position {
        rotateX(x.toFloat() * delta)
        rotateY(y.toFloat() * delta)
        rotateZ(z.toFloat() * delta)
        return this
    }

    fun addLocalRotation(angles: Vector3, delta: Seconds): Position =
        addLocalRotation(angles.x, angles.y, angles.z, delta)

    fun setLocalRotation(quaternion: Quaternion): Position {
        this.quaternion = quaternion
        return updateMatrix()
    }

    fun setLocalRotation(angles: Vector3): Position = setLocalRotation(angles.x, angles.y, angles.z)

    fun setLocalRotation(x: Degree = rotation.x, y: Degree = rotation.y, z: Degree = rotation.z) = setRotationX(x)
        .setRotationY(y)
        .setRotationZ(z)

    fun addScale(x: Percent = 0f, y: Percent = 0f, z: Percent = 0f, delta: Seconds): Position {
        scaleMatrix *= scale(Float3(x.toFloat() * delta, y.toFloat() * delta, z.toFloat() * delta))
        return updateMatrix()
    }

    fun addScale(scale: Vector3, delta: Seconds): Position = addScale(scale.x, scale.y, scale.z, delta)

    fun setScale(x: Percent = scale.x, y: Percent = scale.y, z: Percent = scale.z): Position {
        return addScale(x.toFloat() - scale.x, y.toFloat() - scale.y, z.toFloat() - scale.z, 1f)
    }

    fun setScale(scale: Vector3): Position = setScale(scale.x, scale.y, scale.z)

    fun setGlobalTranslation(
        position: Vector3
    ) = setGlobalTranslation(position.x, position.y, position.z)

    fun setGlobalTranslation(
        x: Coordinate = translation.x,
        y: Coordinate = translation.y,
        z: Coordinate = translation.z
    ): Position {
        translationMatrix = translation(Float3(x.toFloat(), y.toFloat(), z.toFloat()))
        return updateMatrix()
    }

    fun addGlobalTranslation(
        x: Coordinate = 0,
        y: Coordinate = 0,
        z: Coordinate = 0,
        delta: Seconds = 0f
    ): Position {
        translationMatrix *= translation(Float3(x.toFloat() * delta, y.toFloat() * delta, z.toFloat() * delta))
        return updateMatrix()
    }

    fun addLocalTranslation(x: Coordinate = 0, y: Coordinate = 0, z: Coordinate = 0, delta: Seconds = 0f): Position {
        val translated =
            transformation * translation(Float3(x.toFloat() * delta, y.toFloat() * delta, z.toFloat() * delta))
        translationMatrix = translation(translated.translation)
        return updateMatrix()
    }

    fun addImmediateLocalTranslation(x: Coordinate = 0, y: Coordinate = 0, z: Coordinate = 0) =
        addLocalTranslation(x, y, z, 1f)

    private fun setRotationX(angle: Degree): Position {
        return rotateX(angle.toFloat() - rotation.x)
    }

    private fun setRotationY(angle: Degree): Position {
        return rotateY(angle.toFloat() - rotation.y)
    }

    private fun setRotationZ(angle: Degree): Position {
        return rotateZ(angle.toFloat() - rotation.z)
    }

    private fun rotateX(angle: Degree): Position {
        val asFloat = angle.toFloat() * way
        quaternion *= fromEulers(1f, 0f, 0f, -asFloat)
        return updateMatrix()
    }

    private fun rotateY(angle: Degree): Position {
        val asFloat = angle.toFloat() * way
        quaternion *= fromEulers(0f, 1f, 0f, -asFloat)
        return updateMatrix()
    }

    private fun rotateZ(angle: Degree): Position {
        val asFloat = angle.toFloat() * way
        quaternion *= fromEulers(0f, 0f, 1f, -asFloat)
        return updateMatrix()
    }

    private fun updateMatrix(): Position {
        quaternion = normalize(quaternion)
        transformation = translationMatrix * Mat4.from(quaternion) * scaleMatrix
        transformation.translation.run {
            translation.set(x, y, z)
        }
        transformation.rotation.run {
            rotation.set(x, y, z)
        }
        transformation.scale.run {
            scale.set(x, y, z)
        }
        return this
    }
}

/** Multiplies this quaternion with another one in the form of this = this * other
 *
 * @param other Quaternion to multiply with
 * @return This quaternion for chaining
 */
fun Quaternion.mul(other: Quaternion): Quaternion {
    val newX: Float = this.w * other.x + this.x * other.w + this.y * other.z - this.z * other.y
    val newY: Float = this.w * other.y + this.y * other.w + this.z * other.x - this.x * other.z
    val newZ: Float = this.w * other.z + this.z * other.w + this.x * other.y - this.y * other.x
    val newW: Float = this.w * other.w - this.x * other.x - this.y * other.y - this.z * other.z
    return Quaternion(newX, newY, newZ, newW)
}

operator fun Quaternion.times(other: Quaternion): Quaternion = this.mul(other)

fun fromEulers(x: Float, y: Float, z: Float, angle: Degree): Quaternion {
    var d: Float = Vector3(x, y, z).length()
    if (d == 0f) return Quaternion.identity()
    d = 1f / d
    val radians = radians(angle.toFloat())
    val l_ang: Float = if (radians < 0) TWO_PI - -radians % TWO_PI else radians % TWO_PI
    val l_sin = sin(l_ang / 2f)
    return normalize(Quaternion(d * x * l_sin, d * y * l_sin, d * z * l_sin, cos(l_ang / 2f)))
}
