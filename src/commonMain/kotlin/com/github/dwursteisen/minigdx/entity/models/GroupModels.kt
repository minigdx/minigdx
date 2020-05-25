package com.github.dwursteisen.minigdx.entity.models

import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.Quaternion
import com.github.dwursteisen.minigdx.Coordinate
import com.github.dwursteisen.minigdx.Degree
import com.github.dwursteisen.minigdx.Percent
import com.github.dwursteisen.minigdx.entity.CanDraw
import com.github.dwursteisen.minigdx.entity.CanMove
import com.github.dwursteisen.minigdx.entity.delegate.Movable
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.dwursteisen.minigdx.shaders.ShaderProgram

class GroupModels() : CanMove, CanDraw {

    private val delegate: CanMove = Movable()

    private val models: MutableList<CanMove> = mutableListOf()

    private val draws: MutableList<CanDraw> = mutableListOf()

    fun <T> add(child: T) where T : CanMove, T : CanDraw {
        models.add(child)
        draws.add(child)
    }

    override fun draw(shader: ShaderProgram) {
        models.forEachIndexed { index, it ->
            val mat = it.modelMatrix
            it.modelMatrix = delegate.modelMatrix * it.modelMatrix
            draws[index].draw(shader)
            it.modelMatrix = mat
        }
    }

    override var modelMatrix: Mat4 = delegate.modelMatrix
    override val rotation: Vector3 = delegate.rotation
    override val position: Vector3 = delegate.position
    override val scale: Vector3 = delegate.scale

    override fun rotate(x: Degree, y: Degree, z: Degree): CanMove {
        delegate.rotate(x, y, z)
        return this
    }

    override fun rotateX(angle: Degree): CanMove {
        delegate.rotateX(angle)
        return this
    }

    override fun rotateY(angle: Degree): CanMove {
        delegate.rotateY(angle)
        return this
    }

    override fun rotateZ(angle: Degree): CanMove {
        delegate.rotateZ(angle)
        return this
    }

    override fun setRotation(quaternion: Quaternion): CanMove {
        delegate.setRotation(quaternion)
        return this
    }

    override fun setRotationX(angle: Degree): CanMove {
        delegate.setRotationX(angle)
        return this
    }

    override fun setRotationY(angle: Degree): CanMove {
        delegate.setRotationY(angle)
        return this
    }

    override fun setRotationZ(angle: Degree): CanMove {
        delegate.setRotationZ(angle)
        return this
    }

    override fun translate(x: Coordinate, y: Coordinate, z: Coordinate): CanMove {
        delegate.translate(x, y, z)
        return this
    }

    override fun setTranslate(x: Coordinate, y: Coordinate, z: Coordinate): CanMove {
        delegate.setTranslate(x, y, z)
        return this
    }

    override fun scale(x: Percent, y: Percent, z: Percent): CanMove {
        delegate.scale(x, y, z)
        return this
    }

    override fun setScale(x: Percent, y: Percent, z: Percent): CanMove {
        delegate.setScale(x, y, z)
        return this
    }
}
