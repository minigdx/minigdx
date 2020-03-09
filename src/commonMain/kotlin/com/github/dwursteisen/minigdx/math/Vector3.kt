package com.github.dwursteisen.minigdx.math

import com.curiouscreature.kotlin.math.Quaternion
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.sign

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

    fun add(x: Number, y: Number, z: Number): Vector3 {
        this.x += x.toFloat()
        this.y += y.toFloat()
        this.z += z.toFloat()
        return this
    }

    fun add(other: Vector3) = add(other.x, other.y, other.z)

    companion object {

        val X = Vector3(1, 0, 0)
        val Y = Vector3(0, 1, 0)
        val Z = Vector3(0, 0, 1)
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
