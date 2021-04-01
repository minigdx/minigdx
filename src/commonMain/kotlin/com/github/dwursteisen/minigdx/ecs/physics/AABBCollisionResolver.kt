package com.github.dwursteisen.minigdx.ecs.physics

import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox

class AABBCollisionResolver : CollisionResolver {

    override fun collide(boxA: BoundingBox, boxB: BoundingBox): Boolean {
        val minXA = boxA.min.x
        val maxXA = boxA.max.x
        val minYA = boxA.min.y
        val maxYA = boxA.max.y
        val minZA = boxA.min.z
        val maxZA = boxA.max.z

        val minXB = boxB.min.x
        val maxXB = boxB.max.x
        val minYB = boxB.min.y
        val maxYB = boxB.max.y
        val minZB = boxB.min.z
        val maxZB = boxB.max.z

        val xCollide = minXA < maxXB && maxXA > minXB
        val yCollide = minYA < maxYB && maxYA > minYB
        val zCollide = minZA < maxZB && maxZA > minZB

        return (xCollide && yCollide && zCollide)
    }
}
