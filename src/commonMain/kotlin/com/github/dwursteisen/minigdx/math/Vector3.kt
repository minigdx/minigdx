package com.github.dwursteisen.minigdx.math

import com.curiouscreature.kotlin.math.Degrees
import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Quaternion
import com.curiouscreature.kotlin.math.rotation
import com.curiouscreature.kotlin.math.translation
import com.github.dwursteisen.minigdx.Percent
import com.github.dwursteisen.minigdx.toPercent
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.sign
import kotlin.math.sqrt

data class Vector3(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f) {

    inline var roll: Float
        get() = x
        set(value) {
            x = value
        }

    inline var pitch: Float
        get() = y
        set(value) {
            y = value
        }

    inline var yaw: Float
        get() = z
        set(value) {
            z = value
        }

    constructor(x: Number, y: Number, z: Number) : this(x.toFloat(), y.toFloat(), z.toFloat())

    /**
     * Add values to the current vector.
     */
    fun add(x: Number = 0f, y: Number = 0f, z: Number = 0f): Vector3 {
        this.x += x.toFloat()
        this.y += y.toFloat()
        this.z += z.toFloat()
        return this
    }

    /**
     * Add [other] vector to the current vector.
     */
    fun add(other: Vector3) = add(other.x, other.y, other.z)

    /**
     * @see [add]
     */
    fun add(other: ImmutableVector3) = add(other.x, other.y, other.z)

    /**
     * Remove [other] to the current vector.
     */
    fun sub(other: Vector3) = add(-other.x, -other.y, -other.z)

    /**
     * @see [sub]
     */
    fun sub(other: ImmutableVector3) = add(-other.x, -other.y, -other.z)

    /**
     * @see [sub]
     */
    fun sub(x: Number = 0f, y: Number = 0f, z: Number = 0f) =
        add(x.toFloat() * -1f, y.toFloat() * -1f, z.toFloat() * -1f)

    /**
     * Return the negate vector:
     * all values are multiplied by -1
     */
    fun negate(): Vector3 {
        this.x = -this.x
        this.y = -this.y
        this.z = -this.z
        return this
    }

    /**
     * Set values to the current vector.
     */
    fun set(x: Number = this.x, y: Number = this.y, z: Number = this.z): Vector3 {
        this.x = x.toFloat()
        this.y = y.toFloat()
        this.z = z.toFloat()
        return this
    }

    /**
     * Set values of [other] vector to the current vector.
     */
    fun set(other: Vector3) = set(other.x, other.y, other.z)

    /**
     * @see [set]
     */
    fun set(other: ImmutableVector3) = set(other.x, other.y, other.z)

    /**
     * Dot product between another vector.
     *
     * the value help you to know if vectors are
     * in the same direction (> 0),
     * in opposite direction (< 0),
     * or perpendicular (= 0)
     *
     * @see http://blog.wolfire.com/2009/07/linear-algebra-for-game-developers-part-2/
     */
    fun dot(other: Vector3): Float {
        return x * other.x + y * other.y + z * other.z
    }

    /**
     * @see [dot]
     */
    fun dot(other: ImmutableVector3): Float = dot(other.delegate)

    /**
     * Set the vector as the perpendicular vectors
     */
    fun cross(other: Vector3): Vector3 {
        return this.set(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        )
    }

    /**
     * @see [cross]
     */
    fun cross(other: ImmutableVector3): Vector3 = cross(other.delegate)

    /**
     * Projects this vector onto another vector
     *
     * https://github.com/jMonkeyEngine/jmonkeyengine/blob/master/jme3-core/src/main/java/com/jme3/math/Vector3f.java
     */
    fun project(other: Vector3): Vector3 {
        val n = this.dot(other) // A . B
        val d = other.length2(); // |B|^2
        return set(other).scale(n / d)
    }

    fun project(other: ImmutableVector3): Vector3 = project(other.delegate)

    /**
     * Scale the vector by the scalar value.
     */
    fun scale(scalar: Float): Vector3 {
        this.x *= scalar
        this.y *= scalar
        this.z *= scalar
        return this
    }

    /**
     * Rotate the vector
     */
    fun rotate(x: Percent, y: Percent, z: Percent, angle: Degrees): Vector3 {
        val result = rotation(Float3(x.toPercent(), y.toPercent(), z.toPercent()), angle) * translation(this.toFloat3())
        set(result.translation.x, result.translation.y, result.translation.z)
        return this
    }

    /**
     * Rotate the vector by the given quaternion
     *
     * @see: https://gamedev.stackexchange.com/questions/28395/rotating-vector3-by-a-quaternion
     */
    fun rotate(q: Quaternion): Vector3 {
        // Extract the vector part of the quaternion
        val u = Vector3(q.x, q.y, q.z)

        // Extract the scalar part of the quaternion
        val s = q.w

        // Do the math
        val a = u.copy().scale(2.0f * u.dot(this))
        val b = this.copy().scale(s * s - u.dot(u))
        val c = u.copy().cross(this).scale(2.0f * s)
        return this.set(a.add(b).add(c))
    }

    /**
     * Create a normal vector to the current one
     */
    fun normal(): Vector3 {
        return Vector3(z, y, -x)
    }

    /**
     * Normalize the vector (the vector will have a length of 1)
     */
    fun normalize(): Vector3 {
        val len2: Float = x * x + y * y + z * z
        return if (len2 == 0f || len2 == 1f) {
            this
        } else {
            val scalar = 1f / sqrt(len2)
            Vector3(x * scalar, y * scalar, z * scalar)
        }
    }

    /**
     * Return the length of the vector
     */
    fun length(): Float {
        return sqrt(length2())
    }

    /**
     * return the length as powered of two.
     *
     * It's more efficient when you just need to compare distance between objects than
     * knowing the actual distance
     */
    fun length2(): Float {
        return x * x + y * y + z * z
    }

    /**
     * Distance from this vector to [other] vector.
     */
    fun dist(other: Vector3): Float {
        return sqrt(dist2(other))
    }

    fun dist(other: ImmutableVector3): Float = dist(other.delegate)

    /**
     * Distance from this vector to [other] vector squared.
     * Can be use instead of [dist] to compare vector with
     * a faster computation method.
     *
     */
    fun dist2(other: Vector3): Float {
        val a = other.x - x
        val b = other.y - y
        val c = other.z - z
        return a * a + b * b + c * c
    }

    fun dist2(other: ImmutableVector3) = dist2(other.delegate)

    fun toFloat3(): Float3 = Float3(x, y, z)

    companion object {

        val X = Vector3(1, 0, 0)
        val Y = Vector3(0, 1, 0)
        val MINUS_X = Vector3(-1, 0, 0)
        val MINUS_Y = Vector3(0, -1, 0)
        val Z = Vector3(0, 0, 1)
        val ZERO = Vector3(0, 0, 0)

        val FORWARD = Z
        val UP = Y
    }
}

// https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles
fun Quaternion.toVector3(): Vector3 {
    val q = this
    val angles = Vector3()
    // roll (x-axis rotation)
    val sinrCosp = 2 * (q.w * q.x + q.y * q.z)
    val cosrCosp = 1 - 2 * (q.x * q.x + q.y * q.y)
    angles.roll = atan2(sinrCosp, cosrCosp)

    // pitch (y-axis rotation)
    val sinp = 2 * (q.w * q.y - q.z * q.x)
    if (abs(sinp) >= 1)
        angles.pitch = copysign(PI.toFloat() / 2f, sinp); // use 90 degrees if out of range
    else
        angles.pitch = asin(sinp)

    // yaw (z-axis rotation)
    val sinyCosp = 2 * (q.w * q.z + q.x * q.y)
    val cosyCosp = 1 - 2 * (q.y * q.y + q.z * q.z)
    angles.yaw = atan2(sinyCosp, cosyCosp)

    return angles
}

fun copysign(a: Float, b: Float): Float {
    return sign(b) * a
}

fun Float3.toVector3(): Vector3 = Vector3(x, y, z)
