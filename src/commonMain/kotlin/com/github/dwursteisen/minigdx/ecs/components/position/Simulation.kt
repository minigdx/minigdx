package com.github.dwursteisen.minigdx.ecs.components.position

import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.Quaternion
import com.github.dwursteisen.minigdx.Coordinate
import com.github.dwursteisen.minigdx.Degree
import com.github.dwursteisen.minigdx.Percent
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.CoordinateConverter
import com.github.dwursteisen.minigdx.math.ImmutableVector3
import com.github.dwursteisen.minigdx.math.Vector3

interface Simulation {

    val transformation: Mat4
    val quaternion: Quaternion
    val translation: ImmutableVector3
    val rotation: ImmutableVector3
    val scale: ImmutableVector3

    val localTransformation: Mat4
    val localQuaternion: Quaternion
    val localRotation: ImmutableVector3
    val localTranslation: ImmutableVector3
    val localScale: ImmutableVector3

    fun setLocalTransform(transformation: Mat4): Simulation

    fun addLocalRotation(rotation: Quaternion, delta: Seconds = 1f): Simulation
    fun addLocalRotation(x: Degree = 0, y: Degree = 0, z: Degree = 0, delta: Seconds = 1f): Simulation
    fun addLocalRotation(angles: Vector3, delta: Seconds = 1f): Simulation
    fun setLocalRotation(quaternion: Quaternion): Simulation
    fun setLocalRotation(angles: Vector3): Simulation
    fun setLocalRotation(x: Degree = rotation.x, y: Degree = rotation.y, z: Degree = rotation.z): Simulation

    fun addLocalScale(x: Percent = 0f, y: Percent = 0f, z: Percent = 0f, delta: Seconds = 1f): Simulation
    fun addLocalScale(scale: Vector3, delta: Seconds): Simulation
    fun setLocalScale(x: Percent = localScale.x, y: Percent = localScale.y, z: Percent = localScale.z): Simulation

    fun setGlobalTranslation(translation: Vector3): Simulation
    fun setGlobalTranslation(
        x: Coordinate = translation.x,
        y: Coordinate = translation.y,
        z: Coordinate = translation.z
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
        z: Coordinate = localTranslation.z,
        using: CoordinateConverter = CoordinateConverter.Local
    ): Simulation
    fun addLocalTranslation(
        x: Coordinate = 0,
        y: Coordinate = 0,
        z: Coordinate = 0,
        using: CoordinateConverter = CoordinateConverter.Local,
        delta: Seconds = 1f
    ): Simulation

    fun addRotationAround(
        origin: Vector3,
        x: Degree = 0,
        y: Degree = 0,
        z: Degree = 0,
        delta: Seconds = 1f
    ): Simulation

    fun commit(result: Any? = Unit): SimulationResult

    fun rollback(result: Any? = Unit): SimulationResult
}
