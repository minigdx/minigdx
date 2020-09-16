package com.github.dwursteisen.minigdx.ecs.components

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.Quaternion
import com.curiouscreature.kotlin.math.rotation
import com.curiouscreature.kotlin.math.scale
import com.curiouscreature.kotlin.math.translation
import com.github.dwursteisen.minigdx.Coordinate
import com.github.dwursteisen.minigdx.Degree
import com.github.dwursteisen.minigdx.Percent
import com.github.dwursteisen.minigdx.Seconds
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
    var way: Float = 1f,
    var positionTransformation: Mat4 = transformation * scale(
        Float3(
            1 / transformation.scale.x,
            1 / transformation.scale.y,
            1 / transformation.scale.z
        )
    )
) : Component {

    fun addRotation(x: Degree = 0, y: Degree = 0, z: Degree = 0, delta: Seconds): Position {
        rotateX(x.toFloat() * delta)
        rotateY(y.toFloat() * delta)
        rotateZ(z.toFloat() * delta)
        return this
    }

    fun addRotation(angles: Vector3, delta: Seconds): Position = addRotation(angles.x, angles.y, angles.z, delta)

    fun rotateX(angle: Degree): Position {
        val asFloat = angle.toFloat() * way
        rotation.x += asFloat
        positionTransformation *= rotation(
            Float3(
                -1f,
                0f,
                0f
            ), asFloat
        )
        return updateMatrix()
    }

    fun rotateY(angle: Degree): Position {
        val asFloat = angle.toFloat() * way
        rotation.y += asFloat
        positionTransformation *= rotation(
            Float3(
                0f,
                -1f,
                0f
            ), asFloat
        )
        return updateMatrix()
    }

    fun rotateZ(angle: Degree): Position {
        val asFloat = angle.toFloat() * way
        rotation.z += asFloat
        positionTransformation *= rotation(
            Float3(
                0f,
                0f,
                1f
            ), asFloat
        )
        return updateMatrix()
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

    fun addTranslate(x: Coordinate = 0f, y: Coordinate = 0f, z: Coordinate = 0f, delta: Seconds): Position {
        return translate(x.toFloat() * delta, y.toFloat() * delta, z.toFloat() * delta)
    }

    @Deprecated("Prefer addTranslate", ReplaceWith("addTranslate"))
    fun translate(x: Coordinate = 0f, y: Coordinate = 0f, z: Coordinate = 0f): Position {
        translation.add(x, y, z)
        positionTransformation *= translation(Float3(x.toFloat(), y.toFloat(), z.toFloat()))
        return updateMatrix()
    }

    @Deprecated("Prefer addTranslate", ReplaceWith("addTranslate"))
    fun translate(move: Vector3): Position = translate(move.x, move.y, move.z)

    fun addTranslate(move: Vector3, delta: Seconds): Position {
        return addTranslate(move.x, move.y, move.z, delta)
    }

    fun setTranslate(
        x: Coordinate = translation.x,
        y: Coordinate = translation.y,
        z: Coordinate = translation.z
    ): Position {
        return translate(x.toFloat() - translation.x, y.toFloat() - translation.y, z.toFloat() - translation.z)
    }

    fun setTranslate(move: Vector3): Position = setTranslate(move.x, move.y, move.z)

    fun addScale(x: Percent = 0f, y: Percent = 0f, z: Percent = 0f, delta: Seconds): Position {
        scale.add(x.toFloat() * delta, y.toFloat() * delta, z.toShort() * delta)
        return updateMatrix()
    }

    fun addScale(scale: Vector3, delta: Seconds): Position = addScale(scale.x, scale.y, scale.z, delta)

    fun setScale(x: Percent = scale.x, y: Percent = scale.y, z: Percent = scale.z): Position {
        return addScale(x.toFloat() - scale.x, y.toFloat() - scale.y, z.toFloat() - scale.z, 1f)
    }

    fun setScale(scale: Vector3): Position = setScale(scale.x, scale.y, scale.z)

    private fun updateMatrix(): Position {
        transformation = positionTransformation * scale(Float3(scale.x, scale.y, scale.z))
        return this
    }
}
