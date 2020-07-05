package com.github.dwursteisen.minigdx.ecs.components

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.Quaternion
import com.curiouscreature.kotlin.math.rotation
import com.curiouscreature.kotlin.math.translation
import com.github.dwursteisen.minigdx.Coordinate
import com.github.dwursteisen.minigdx.Degree
import com.github.dwursteisen.minigdx.math.Vector3

data class Position(
    var transformation: Mat4 = Mat4.identity(),
    val translation: Vector3 = transformation.position.let {
        Vector3(
            it.x,
            it.y,
            it.z
        )
    },
    val rotation: Vector3 = transformation.rotation.let {
        Vector3(
            it.x,
            it.y,
            it.z
        )
    },
    val scale: Vector3 = transformation.scale.let {
        Vector3(
            it.x,
            it.y,
            it.z
        )
    },
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
                -1f,
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
                -1f,
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

    fun setRotation(quaternion: Quaternion): Position {
        val rotation = Mat4.from(quaternion)
        setRotationX(rotation.rotation.x)
        setRotationY(rotation.rotation.y)
        setRotationZ(rotation.rotation.z)
        return this
    }

    fun setRotation(angles: Vector3): Position = setRotationX(angles.x)
        .setRotationY(angles.y)
        .setRotationZ(angles.z)

    fun setRotationX(angle: Degree): Position {
        return rotateX(angle.toFloat() - rotation.x)
    }

    fun setRotationY(angle: Degree): Position {
        return rotateY(angle.toFloat() - rotation.y)
    }

    fun setRotationZ(angle: Degree): Position {
        return rotateZ(angle.toFloat() - rotation.z)
    }

    fun translate(x: Coordinate = 0f, y: Coordinate = 0f, z: Coordinate = 0f): Position {
        translation.add(x, y, z)
        transformation *= translation(Float3(x.toFloat(), y.toFloat(), z.toFloat()))
        return this
    }

    fun translate(move: Vector3): Position = translate(move.x, move.y, move.z)

    fun setTranslate(x: Coordinate = translation.x, y: Coordinate = translation.y, z: Coordinate = translation.z): Position {
        return translate(x.toFloat() - translation.x, y.toFloat() - translation.y, z.toFloat() - translation.z)
    }

    fun setTranslate(move: Vector3): Position = setTranslate(move.x, move.y, move.z)

    /*
    fun scale(x: Percent = scale.x, y: Percent = scale.y, z: Percent = scale.z): Position
    fun scale(scale: Vector3): Position = scale(scale.x, scale.y, scale.z)

    fun setScale(x: Percent = 1, y: Percent = 1, z: Percent = 1): Position
    fun setScale(scale: Vector3): Position = setScale(scale.x, scale.y, scale.z)
*/
}
