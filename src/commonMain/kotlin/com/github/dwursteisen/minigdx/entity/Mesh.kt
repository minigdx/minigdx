package com.github.dwursteisen.minigdx.entity

import com.curiouscreature.kotlin.math.Mat4
import com.github.dwursteisen.minigdx.entity.delegate.Movable
import com.github.dwursteisen.minigdx.math.Vector3

enum class DrawType {
    TRIANGLE, LINE
}
class Mesh(
    val name: String = "unknown",
    position: Vector3 = Vector3(),
    rotation: Vector3 = Vector3(),
    modelMatrix: Mat4 = Mat4.identity(),
    val vertices: Array<Vertice> = emptyArray(),
    val verticesOrder: ShortArray,
    val drawType: DrawType = DrawType.TRIANGLE
) : CanCopy<Mesh>, CanMove by Movable(
    position = position,
    rotation = rotation,
    modelMatrix = modelMatrix
) {
    override fun copy() = Mesh(
        name = this.name,
        position = position.copy(),
        rotation = rotation.copy(),
        modelMatrix = modelMatrix.copy(),
        vertices = vertices,
        verticesOrder = verticesOrder,
        drawType = drawType
    )
}
