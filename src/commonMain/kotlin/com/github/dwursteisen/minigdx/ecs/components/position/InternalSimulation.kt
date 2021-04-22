package com.github.dwursteisen.minigdx.ecs.components.position

import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.Quaternion
import com.github.dwursteisen.minigdx.Coordinate
import com.github.dwursteisen.minigdx.Degree
import com.github.dwursteisen.minigdx.Percent
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.math.ImmutableVector3
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.dwursteisen.minigdx.toPercent

typealias RollbackAction = Position.() -> Unit

class InternalSimulation(private val position: Position) : Simulation {

    private val reverseAction = ArrayDeque<RollbackAction>()

    override val transformation: Mat4 = position.transformation
    override val globalTransformation: Mat4 = position.transformation
    override val localTransformation: Mat4 = position.localTransformation
    override val quaternion: Quaternion = position.quaternion
    override val globalTranslation: Vector3 = Vector3()
    override val localTranslation: Vector3 = Vector3()
    override val translation: Vector3 = Vector3()
    override val globalRotation: Vector3 = Vector3()
    override val localRotation: Vector3 = Vector3()
    override val rotation: Vector3 = Vector3()
    override val localScale: ImmutableVector3 = position.localScale
    override val scale: ImmutableVector3 = position.scale
    override val localQuaternion: Quaternion = position.localQuaternion
    override val globalQuaternion: Quaternion = position.localQuaternion

    override fun setLocalTransform(transformation: Mat4): Simulation {
        val currentTransformation = position.localTransformation
        reverseAction.add { position.setLocalTransform(currentTransformation) }
        position.setLocalTransform(transformation)
        return this
    }

    override fun setGlobalTransform(transformation: Mat4): Simulation {
        TODO("Not yet implemented")
    }

    override fun setGlobalRotation(quaternion: Quaternion): Simulation {
        TODO("Not yet implemented")
    }

    override fun setGlobalRotation(x: Degree, y: Degree, z: Degree): Simulation {
        TODO("Not yet implemented")
    }

    override fun addGlobalRotation(x: Degree, y: Degree, z: Degree, delta: Seconds): Simulation {
        TODO("Not yet implemented")
    }

    override fun addLocalRotation(rotation: Quaternion, delta: Seconds): Simulation {
        TODO("Not yet implemented")
    }

    override fun addLocalRotation(x: Degree, y: Degree, z: Degree, delta: Seconds): Simulation {
        TODO("Not yet implemented")
    }

    override fun addLocalRotation(angles: Vector3, delta: Seconds): Simulation {
        TODO("Not yet implemented")
    }

    override fun setLocalRotation(quaternion: Quaternion): Simulation {
        TODO("Not yet implemented")
    }

    override fun setLocalRotation(angles: Vector3): Simulation {
        TODO("Not yet implemented")
    }

    override fun setLocalRotation(x: Degree, y: Degree, z: Degree): Simulation {
        TODO("Not yet implemented")
    }

    override fun addLocalScale(x: Percent, y: Percent, z: Percent, delta: Seconds): Simulation {
        position.addLocalScale(x, y, z, delta)
        reverseAction.add {
            position.addLocalScale(
                x.toPercent() * -1f,
                y.toPercent() * -1f,
                y.toPercent() * -1f,
                delta
            )
        }
        return this
    }

    override fun addLocalScale(scale: Vector3, delta: Seconds): Simulation {
        TODO("Not yet implemented")
    }

    override fun setLocalScale(x: Percent, y: Percent, z: Percent): Simulation {
        TODO("Not yet implemented")
    }

    override fun addGlobalScale(x: Percent, y: Percent, z: Percent, delta: Seconds): Simulation {
        TODO("Not yet implemented")
    }

    override fun setGlobalTranslation(position: Vector3): Simulation {
        TODO("Not yet implemented")
    }

    override fun setGlobalTranslation(x: Coordinate, y: Coordinate, z: Coordinate): Simulation {
        TODO("Not yet implemented")
    }

    override fun addGlobalTranslation(x: Coordinate, y: Coordinate, z: Coordinate, delta: Seconds): Simulation {
        TODO("Not yet implemented")
    }

    override fun setLocalTranslation(x: Coordinate, y: Coordinate, z: Coordinate): Simulation {
        TODO("Not yet implemented")
    }

    override fun addLocalTranslation(x: Coordinate, y: Coordinate, z: Coordinate, delta: Seconds): Simulation {
        TODO("Not yet implemented")
    }

    override fun addImmediateLocalTranslation(x: Coordinate, y: Coordinate, z: Coordinate): Simulation {
        TODO("Not yet implemented")
    }

    override fun addGlobalRotationAround(origin: Vector3, x: Degree, y: Degree, z: Degree, delta: Seconds): Simulation {
        TODO("Not yet implemented")
    }

    override fun commit(result: Any?): SimulationResult = SimulationResult.Commit(result)

    override fun rollback(result: Any?): SimulationResult = SimulationResult.Rollback(result)

    fun rollbackMe() {
        reverseAction.forEach { rollback ->
            position.rollback()
        }
    }
}
