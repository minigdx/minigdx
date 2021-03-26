package com.github.dwursteisen.minigdx.ecs.components.position

import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.Quaternion
import com.github.dwursteisen.minigdx.Coordinate
import com.github.dwursteisen.minigdx.Degree
import com.github.dwursteisen.minigdx.Percent
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.dwursteisen.minigdx.toPercent

typealias RollbackAction = Position.() -> Unit

class InternalSimulation(private val position: Position) : Simulation {

    private val reverseAction = ArrayDeque<RollbackAction>()

    override val transformation: Mat4 = position.transformation
    override val globalTransformation: Mat4 = position.globalTransformation
    override val localTransformation: Mat4 = position.localTransformation
    override val quaternion: Quaternion = position.quaternion
    override val globalTranslation: Vector3 = position.globalTranslation
    override val localTranslation: Vector3 = position.localTranslation
    override val translation: Vector3 = position.translation
    override val globalRotation: Vector3 = position.globalRotation
    override val localRotation: Vector3 = position.localRotation
    override val rotation: Vector3 = position.rotation
    override val globalScale: Vector3 = position.globalScale
    override val localScale: Vector3 = position.localScale
    override val scale: Vector3 = position.scale
    override val localQuaternion: Quaternion = position.localQuaternion
    override val globalQuaternion: Quaternion = position.globalQuaternion

    override fun setLocalTransform(transformation: Mat4): Simulation {
        val currentTransformation = position.localTransformation
        reverseAction.add { position.setLocalTransform(currentTransformation) }
        position.setLocalTransform(transformation)
        return this
    }

    override fun setGlobalTransform(transformation: Mat4): Simulation {
        val currentTransformation = position.globalTransformation
        reverseAction.add { position.setGlobalTransform(currentTransformation) }
        position.setGlobalTransform(transformation)
        return this
    }

    override fun setGlobalRotation(quaternion: Quaternion): Simulation {
        val currentRotation = position.globalQuaternion
        reverseAction.add { position.setGlobalRotation(currentRotation) }
        position.setGlobalRotation(quaternion)
        return this
    }

    override fun setGlobalRotation(x: Degree, y: Degree, z: Degree): Simulation {
        val currentRotation = position.globalQuaternion
        reverseAction.add { position.setGlobalRotation(currentRotation) }
        position.setGlobalRotation(x, y, z)
        return this
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

    override fun setGlobalScale(x: Percent, y: Percent, z: Percent): Simulation {
        TODO("Not yet implemented")
    }

    override fun setGlobalTranslation(position: Vector3): Simulation {
        val currentTransformation = this.position.globalTranslation.copy()
        reverseAction.add { this@InternalSimulation.position.setGlobalTranslation(currentTransformation) }
        this.position.setGlobalTranslation(position)
        return this
    }

    override fun setGlobalTranslation(x: Coordinate, y: Coordinate, z: Coordinate): Simulation {
        val currentTransformation = this.position.globalTranslation.copy()
        reverseAction.add { this@InternalSimulation.position.setGlobalTranslation(currentTransformation) }
        this.position.setGlobalTranslation(x, y, z)
        return this
    }

    override fun addGlobalTranslation(x: Coordinate, y: Coordinate, z: Coordinate, delta: Seconds): Simulation {
        val currentTransformation = this.position.globalTranslation.copy()
        reverseAction.add { position.setGlobalTranslation(currentTransformation) }
        position.addGlobalTranslation(x, y, z, delta)
        return this
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
