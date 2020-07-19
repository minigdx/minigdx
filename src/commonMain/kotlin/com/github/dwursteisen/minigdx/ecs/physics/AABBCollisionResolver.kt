package com.github.dwursteisen.minigdx.ecs.physics

import com.curiouscreature.kotlin.math.Mat4
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox

class AABBCollisionResolver : CollisionResolver {

    override fun collide(entityA: BoundingBox, positionA: Mat4, entityB: BoundingBox, positionB: Mat4): Boolean {
        // FIXME: Scale should be applied on the box
        val translationA = positionA.translation
        val translationB = positionB.translation

        val minXA = entityA.vertices.minBy { it.position.x }!!.position.x + translationA.x
        val maxXA = entityA.vertices.maxBy { it.position.x }!!.position.x + translationA.x
        val minYA = entityA.vertices.minBy { it.position.y }!!.position.y + translationA.y
        val maxYA = entityA.vertices.maxBy { it.position.y }!!.position.y + translationA.y
        val minZA = entityA.vertices.minBy { it.position.z }!!.position.z + translationA.z
        val maxZA = entityA.vertices.maxBy { it.position.z }!!.position.z + translationA.z

        val minXB = entityB.vertices.minBy { it.position.x }!!.position.x + translationB.x
        val maxXB = entityB.vertices.maxBy { it.position.x }!!.position.x + translationB.x
        val minYB = entityB.vertices.minBy { it.position.y }!!.position.y + translationB.y
        val maxYB = entityB.vertices.maxBy { it.position.y }!!.position.y + translationB.y
        val minZB = entityB.vertices.minBy { it.position.z }!!.position.z + translationB.z
        val maxZB = entityB.vertices.maxBy { it.position.z }!!.position.z + translationB.z

        val xCollide = minXA < maxXB && maxXA > minXB
        val yCollide = minYA < maxYB && maxYA > minYB
        val zCollide = minZA < maxZB && maxZA > minZB

        return (xCollide && yCollide && zCollide)
    }
}
