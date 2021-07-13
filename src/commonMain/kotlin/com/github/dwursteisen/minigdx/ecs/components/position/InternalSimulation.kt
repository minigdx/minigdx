package com.github.dwursteisen.minigdx.ecs.components.position

import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.Quaternion
import com.github.dwursteisen.minigdx.Coordinate
import com.github.dwursteisen.minigdx.Degree
import com.github.dwursteisen.minigdx.Percent
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.CoordinateConverter
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.math.ImmutableVector3
import com.github.dwursteisen.minigdx.math.Vector3

class InternalSimulation(private val position: Position) : Simulation {

    private val initialTransformation = position.localTransformation

    override val transformation: Mat4 = position.transformation
    override val localTransformation: Mat4 = position.localTransformation
    override val quaternion: Quaternion = position.quaternion
    override val localScale: ImmutableVector3 = position.localScale
    override val scale: ImmutableVector3 = position.scale
    override val localQuaternion: Quaternion = position.localQuaternion
    override val translation: ImmutableVector3 = position.translation
    override val rotation: ImmutableVector3 = position.rotation
    override val localRotation: ImmutableVector3 = position.localRotation
    override val localTranslation: ImmutableVector3 = position.localTranslation

    override fun setLocalTransform(transformation: Mat4): Simulation {
        position.setLocalTransform(transformation)
        return this
    }

    override fun addLocalRotation(rotation: Quaternion, delta: Seconds): Simulation {
        position.addLocalRotation(rotation, delta)
        return this
    }

    override fun addLocalRotation(x: Degree, y: Degree, z: Degree, delta: Seconds): Simulation {
        position.addLocalRotation(x, y, z, delta)
        return this
    }

    override fun addLocalRotation(angles: Vector3, delta: Seconds): Simulation {
        position.addLocalRotation(angles, delta)
        return this
    }

    override fun setLocalRotation(quaternion: Quaternion): Simulation {
        position.setLocalRotation(quaternion)
        return this
    }

    override fun setLocalRotation(angles: Vector3): Simulation {
        position.setLocalRotation(angles)
        return this
    }

    override fun setLocalRotation(x: Degree, y: Degree, z: Degree): Simulation {
        position.setLocalRotation(x, y, z)
        return this
    }

    override fun addLocalScale(x: Percent, y: Percent, z: Percent, delta: Seconds): Simulation {
        position.addLocalScale(x, y, z, delta)
        return this
    }

    override fun addLocalScale(scale: Vector3, delta: Seconds): Simulation {
        position.addLocalScale(scale, delta)
        return this
    }

    override fun setLocalScale(x: Percent, y: Percent, z: Percent): Simulation {
        position.setLocalScale(x, y, z)
        return this
    }

    override fun setGlobalTranslation(translation: Vector3): Simulation {
        position.setGlobalTranslation(translation)
        return this
    }

    override fun setGlobalTranslation(x: Coordinate, y: Coordinate, z: Coordinate): Simulation {
        position.setGlobalTranslation(x, y, z)
        return this
    }

    override fun addGlobalTranslation(x: Coordinate, y: Coordinate, z: Coordinate, delta: Seconds): Simulation {
        position.addGlobalTranslation(x, y, z, delta)
        return this
    }

    override fun setLocalTranslation(x: Coordinate, y: Coordinate, z: Coordinate, using: CoordinateConverter): Simulation {
        position.setLocalTranslation(x, y, z, using)
        return this
    }

    override fun addLocalTranslation(x: Coordinate, y: Coordinate, z: Coordinate, using: CoordinateConverter, delta: Seconds): Simulation {
        position.addLocalTranslation(x, y, z, using, delta)
        return this
    }

    override fun addRotationAround(origin: Vector3, x: Degree, y: Degree, z: Degree, delta: Seconds): Simulation {
        position.addRotationAround(origin, x, y, z, delta)
        return this
    }

    override fun commit(result: Any?): SimulationResult = SimulationResult.Commit(result)

    override fun rollback(result: Any?): SimulationResult = SimulationResult.Rollback(result)

    fun rollbackMe() {
        position.setLocalTransform(initialTransformation)
    }
}
