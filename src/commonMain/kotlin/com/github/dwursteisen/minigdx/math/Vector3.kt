package com.github.dwursteisen.minigdx.math

import com.curiouscreature.kotlin.math.Quaternion
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

    fun add(x: Number = 0f, y: Number = 0f, z: Number = 0f): Vector3 {
        this.x += x.toFloat()
        this.y += y.toFloat()
        this.z += z.toFloat()
        return this
    }

    fun add(other: Vector3) = add(other.x, other.y, other.z)

    fun sub(other: Vector3) = add(-other.x, -other.y, -other.z)

    fun sub(x: Number = 0f, y: Number = 0f, z: Number = 0f) =
        add(x.toFloat() * -1f, y.toFloat() * -1f, z.toFloat() * -1f)

    fun set(x: Number = 0f, y: Number = 0f, z: Number = 0f) {
        this.x = x.toFloat()
        this.y = y.toFloat()
        this.z = z.toFloat()
    }

    fun set(other: Vector3) = set(other.x, other.y, other.z)

    fun dot(other: Vector3): Float {
        return x * other.x + y * other.y + z * other.z
    }

    fun normal(): Vector3 {
        return Vector3(z, y, -x)
    }

    fun normalize(): Vector3 {
        val len2: Float = x * x + y * y + z * z
        return if (len2 == 0f || len2 == 1f) {
            this
        } else {
            val scalar = 1f / sqrt(len2)
            Vector3(x * scalar, y * scalar, z * scalar)
        }
    }

    fun length(): Float {
        return sqrt(length2())
    }

    fun length2(): Float {
        return x * x + y * y + z * z
    }

    fun dist(other: Vector3): Float {
        return sqrt(dist2(other))
    }

    fun dist2(other: Vector3): Float {
        val a = other.x - x
        val b = other.y - y
        val c = other.z - z
        return a * a + b * b + c * c
    }

    companion object {

        val X = Vector3(1, 0, 0)
        val Y = Vector3(0, 1, 0)
        val Z = Vector3(0, 0, 1)
        val ZERO = Vector3(0, 0, 0)
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
