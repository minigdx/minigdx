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
    private var translationMatrix: Mat4 = translation(transformation.translation),
    private var rotationMatrix: Mat4 = rotation(transformation.rotation),
    private var scaleMatrix: Mat4 = scale(transformation.scale),
    var way: Float = 1f
) : Component {

    val translation: Vector3 = Vector3()
    val rotation: Vector3 = Vector3()
    val scale: Vector3 = Vector3()

    fun addLocalRotation(x: Degree = 0, y: Degree = 0, z: Degree = 0, delta: Seconds): Position {
        rotateX(x.toFloat() * delta)
        rotateY(y.toFloat() * delta)
        rotateZ(z.toFloat() * delta)
        return this
    }

    fun addLocalRotation(angles: Vector3, delta: Seconds): Position =
        addLocalRotation(angles.x, angles.y, angles.z, delta)

    fun setLocalRotation(quaternion: Quaternion): Position {
        val rotation = Mat4.from(quaternion)
        setRotationX(rotation.rotation.x)
        setRotationY(rotation.rotation.y)
        setRotationZ(rotation.rotation.z)
        return this
    }

    fun setLocalRotation(angles: Vector3): Position = setRotationX(angles.x)
        .setRotationY(angles.y)
        .setRotationZ(angles.z)

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
        rotationMatrix *= rotation(Float3(-asFloat, 0f, 0f))
        return updateMatrix()
    }

    private fun rotateY(angle: Degree): Position {
        val asFloat = angle.toFloat() * way
        rotationMatrix *= rotation(Float3(0f, -asFloat, 0f))
        return updateMatrix()
    }

    private fun rotateZ(angle: Degree): Position {
        val asFloat = angle.toFloat() * way
        rotationMatrix *= rotation(Float3(0f, 0f, -asFloat))
        return updateMatrix()
    }

    private fun updateMatrix(): Position {
        transformation = translationMatrix * rotationMatrix * scaleMatrix
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
