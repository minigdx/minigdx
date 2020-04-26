package com.github.dwursteisen.minigdx.entity

import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.Quaternion
import com.github.dwursteisen.minigdx.math.Vector3

typealias Degree = Number
typealias Coordinate = Number
typealias Factor = Number

interface CanMove {

    var modelMatrix: Mat4

    val rotation: Vector3
    val position: Vector3
    val scale: Vector3

    fun rotate(x: Degree = 0, y: Degree = 0, z: Degree = 0): CanMove
    fun rotate(angles: Vector3): CanMove

    fun rotateX(angle: Degree): CanMove
    fun rotateY(angle: Degree): CanMove
    fun rotateZ(angle: Degree): CanMove

    fun setRotation(quaternion: Quaternion): CanMove
    fun setRotationX(angle: Degree): CanMove
    fun setRotationY(angle: Degree): CanMove
    fun setRotationZ(angle: Degree): CanMove

    fun translate(x: Coordinate = 0f, y: Coordinate = 0f, z: Coordinate = 0f): CanMove
    fun translate(move: Vector3): CanMove = translate(move.x, move.y, move.z)

    fun setTranslate(x: Coordinate, y: Coordinate, z: Coordinate): CanMove
    fun setTranslate(move: Vector3): CanMove = setTranslate(move.x, move.y, move.z)

    fun scale(x: Factor = scale.x, y: Factor = scale.y, z: Factor = scale.z): CanMove
    fun scale(scale: Vector3): CanMove = scale(scale.x, scale.y, scale.z)

    fun setScale(x: Factor = 1, y: Factor = 1, z: Factor = 1): CanMove
    fun setScale(scale: Vector3): CanMove = setScale(scale.x, scale.y, scale.z)
}
