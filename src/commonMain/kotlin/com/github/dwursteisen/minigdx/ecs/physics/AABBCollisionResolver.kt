package com.github.dwursteisen.minigdx.ecs.physics

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.translation
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox

class AABBCollisionResolver : CollisionResolver {

    override fun collide(entityA: BoundingBox, positionA: Mat4, entityB: BoundingBox, positionB: Mat4): Boolean {
        val a = entityA.vertices.map { v ->
            positionA * translation(Float3(v.position.x, v.position.y, v.position.z))
        }.map {
            it.position
        }
        val b = entityB.vertices.map { v ->
            positionB * translation(Float3(v.position.x, v.position.y, v.position.z))
        }.map {
            it.position
        }

        val minXA = a.minByOrNull { it.x }!!.x
        val maxXA = a.maxByOrNull { it.x }!!.x
        val minYA = a.minByOrNull { it.y }!!.y
        val maxYA = a.maxByOrNull { it.y }!!.y
        val minZA = a.minByOrNull { it.z }!!.z
        val maxZA = a.maxByOrNull { it.z }!!.z

        val minXB = b.minByOrNull { it.x }!!.x
        val maxXB = b.maxByOrNull { it.x }!!.x
        val minYB = b.minByOrNull { it.y }!!.y
        val maxYB = b.maxByOrNull { it.y }!!.y
        val minZB = b.minByOrNull { it.z }!!.z
        val maxZB = b.maxByOrNull { it.z }!!.z

        val xCollide = minXA < maxXB && maxXA > minXB
        val yCollide = minYA < maxYB && maxYA > minYB
        val zCollide = minZA < maxZB && maxZA > minZB

        return (xCollide && yCollide && zCollide)
    }
}
