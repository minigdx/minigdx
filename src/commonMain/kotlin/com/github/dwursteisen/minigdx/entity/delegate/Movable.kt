package com.github.dwursteisen.minigdx.entity.delegate

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.Quaternion
import com.curiouscreature.kotlin.math.rotation
import com.curiouscreature.kotlin.math.translation
import com.github.dwursteisen.minigdx.Coordinate
import com.github.dwursteisen.minigdx.Degree
import com.github.dwursteisen.minigdx.Percent
import com.github.dwursteisen.minigdx.entity.CanMove
import com.github.dwursteisen.minigdx.math.Vector3

/**
 * Computation of moving an entity.
 */
class Movable(
    override var modelMatrix: Mat4 = Mat4.identity(),
    override val rotation: Vector3 = Vector3(),
    override val position: Vector3 = Vector3(),
    override val scale: Vector3 = Vector3(1f, 1f, 1f)
) : CanMove {

    override fun rotate(x: Degree, y: Degree, z: Degree): CanMove {
        rotateX(x.toFloat())
        rotateY(y.toFloat())
        rotateZ(z.toFloat())
        return this
    }

    override fun rotate(angles: Vector3): CanMove {
        rotateX(angles.x)
        rotateY(angles.y)
        rotateZ(angles.z)
        return this
    }

    override fun rotateX(angle: Degree): CanMove {
        val asFloat = angle.toFloat()
        rotation.x += asFloat
        modelMatrix *= rotation(
            Float3(
                1f,
                0f,
                0f
            ), asFloat
        )
        return this
    }

    override fun rotateY(angle: Degree): CanMove {
        val asFloat = angle.toFloat()
        rotation.y += asFloat
        modelMatrix *= rotation(
            Float3(
                0f,
                1f,
                0f
            ), asFloat
        )
        return this
    }

    override fun rotateZ(angle: Degree): CanMove {
        val asFloat = angle.toFloat()
        rotation.z += asFloat
        modelMatrix *= rotation(
            Float3(
                0f,
                0f,
                1f
            ), asFloat
        )
        return this
    }

    override fun setRotationX(angle: Degree): CanMove {
        val toRotate = angle.toFloat() - rotation.x
        return rotateX(toRotate)
    }

    override fun setRotationY(angle: Degree): CanMove {
        val toRotate = angle.toFloat() - rotation.y
        return rotateY(toRotate)
    }

    override fun setRotationZ(angle: Degree): CanMove {
        val toRotate = angle.toFloat() - rotation.z
        return rotateZ(toRotate)
    }

    override fun setRotation(quaternion: Quaternion): CanMove {
        val angles = quaternion.toEulerAngles()
        return setRotationX(angles.x)
            .setRotationY(angles.y)
            .setRotationZ(angles.z)
    }

    override fun translate(x: Coordinate, y: Coordinate, z: Coordinate): CanMove {
        position.add(x.toFloat(), y.toFloat(), z.toFloat())
        modelMatrix *= translation(
            Float3(
                x.toFloat(),
                y.toFloat(),
                z.toFloat()
            )
        )
        return this
    }

    override fun setTranslate(x: Coordinate, y: Coordinate, z: Coordinate): CanMove {
        val toX = x.toFloat() - position.x
        val toY = y.toFloat() - position.y
        val toZ = z.toFloat() - position.z
        return translate(toX, toY, toZ)
    }

    override fun scale(x: Coordinate, y: Coordinate, z: Coordinate): CanMove {
        scale.add(x.toFloat(), y.toFloat(), z.toFloat())
        modelMatrix *= com.curiouscreature.kotlin.math.scale(Float3(x.toFloat(), y.toFloat(), z.toFloat()))
        return this
    }

    override fun setScale(x: Percent, y: Percent, z: Percent): CanMove {
        val toX = x.toFloat() - scale.x
        val toY = y.toFloat() - scale.y
        val toZ = z.toFloat() - scale.z
        return scale(toX, toY, toZ)
    }
}
