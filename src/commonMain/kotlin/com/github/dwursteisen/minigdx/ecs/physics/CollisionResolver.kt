package com.github.dwursteisen.minigdx.ecs.physics

import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.ecs.entities.Entity

interface CollisionResolver {

    fun collide(entityA: Entity, entityB: Entity): Boolean {
        val boxA = entityA.get(BoundingBox::class)
        val boxB = entityB.get(BoundingBox::class)
        return collide(boxA, boxB)
    }

    fun collide(entityA: BoundingBox, entityB: BoundingBox): Boolean
}
