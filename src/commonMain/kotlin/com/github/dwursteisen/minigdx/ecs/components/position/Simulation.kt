package com.github.dwursteisen.minigdx.ecs.components.position

import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.Quaternion
import com.github.dwursteisen.minigdx.Coordinate
import com.github.dwursteisen.minigdx.Degree
import com.github.dwursteisen.minigdx.Percent
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.math.Vector3

interface Simulation {

    /**
     * Transformation given by the global transformation and then the local transformation.
     */
    val transformation: Mat4
    val globalTransformation: Mat4
    val localTransformation: Mat4
    val quaternion: Quaternion
    val globalTranslation: Vector3
    val localTranslation: Vector3
    val translation: Vector3
    val globalRotation: Vector3
    val localRotation: Vector3
    val rotation: Vector3

    val globalScale: Vector3
    val localScale: Vector3
    val scale: Vector3

    val localQuaternion: Quaternion
    val globalQuaternion: Quaternion

    fun setLocalTransform(transformation: Mat4): Simulation

    fun setGlobalTransform(transformation: Mat4): Simulation

    fun setGlobalRotation(quaternion: Quaternion): Simulation

    fun setGlobalRotation(
        x: Degree = globalRotation.x,
        y: Degree = globalRotation.y,
        z: Degree = globalRotation.z
    ): Simulation

    fun addGlobalRotation(
        x: Degree = 0f,
        y: Degree = 0f,
        z: Degree = 0f,
        delta: Seconds = 1f
    ): Simulation

    fun addLocalRotation(rotation: Quaternion, delta: Seconds = 1f): Simulation

    fun addLocalRotation(x: Degree = 0, y: Degree = 0, z: Degree = 0, delta: Seconds = 1f): Simulation

    fun addLocalRotation(angles: Vector3, delta: Seconds = 1f): Simulation

    fun setLocalRotation(quaternion: Quaternion): Simulation

    fun setLocalRotation(angles: Vector3): Simulation
    fun setLocalRotation(x: Degree = rotation.x, y: Degree = rotation.y, z: Degree = rotation.z): Simulation

    fun addLocalScale(x: Percent = 0f, y: Percent = 0f, z: Percent = 0f, delta: Seconds = 1f): Simulation

    fun addLocalScale(scale: Vector3, delta: Seconds): Simulation

    fun setLocalScale(x: Percent = localScale.x, y: Percent = localScale.y, z: Percent = localScale.z): Simulation

    fun addGlobalScale(x: Percent = 0f, y: Percent = 0f, z: Percent = 0f, delta: Seconds = 1f): Simulation

    fun setGlobalScale(x: Percent = globalScale.x, y: Percent = globalScale.y, z: Percent = globalScale.z): Simulation

    fun setGlobalTranslation(position: Vector3): Simulation

    fun setGlobalTranslation(
        x: Coordinate = globalTranslation.x,
        y: Coordinate = globalTranslation.y,
        z: Coordinate = globalTranslation.z
    ): Simulation

    fun addGlobalTranslation(
        x: Coordinate = 0,
        y: Coordinate = 0,
        z: Coordinate = 0,
        delta: Seconds = 1f
    ): Simulation

    fun setLocalTranslation(
        x: Coordinate = localTranslation.x,
        y: Coordinate = localTranslation.y,
        z: Coordinate = localTranslation.z
    ): Simulation

    fun addLocalTranslation(x: Coordinate = 0, y: Coordinate = 0, z: Coordinate = 0, delta: Seconds = 1f): Simulation

    fun addImmediateLocalTranslation(x: Coordinate = 0, y: Coordinate = 0, z: Coordinate = 0): Simulation

    fun addGlobalRotationAround(
        origin: Vector3,
        x: Degree = 0,
        y: Degree = 0,
        z: Degree = 0,
        delta: Seconds = 1f
    ): Simulation

    fun commit(result: Any? = Unit): SimulationResult

    fun rollback(result: Any? = Unit): SimulationResult
}
