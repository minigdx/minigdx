package com.github.dwursteisen.minigdx.ecs.physics

import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox

class AABBCollisionResolver : CollisionResolver {

    override fun collide(entityA: BoundingBox, entityB: BoundingBox): Boolean {
        val minXA = entityA.min.x
        val maxXA = entityA.max.x
        val minYA = entityA.min.y
        val maxYA = entityA.max.y
        val minZA = entityA.min.z
        val maxZA = entityA.max.z

        val minXB = entityB.min.x
        val maxXB = entityB.max.x
        val minYB = entityB.min.y
        val maxYB = entityB.max.y
        val minZB = entityB.min.z
        val maxZB = entityB.max.z

        val xCollide = minXA < maxXB && maxXA > minXB
        val yCollide = minYA < maxYB && maxYA > minYB
        val zCollide = minZA < maxZB && maxZA > minZB

        return (xCollide && yCollide && zCollide)
    }
}
