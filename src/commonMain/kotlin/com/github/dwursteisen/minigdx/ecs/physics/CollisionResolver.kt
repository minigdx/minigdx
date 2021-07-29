package com.github.dwursteisen.minigdx.ecs.physics

import com.github.dwursteisen.minigdx.ecs.components.BoundingBoxComponent
import com.github.dwursteisen.minigdx.ecs.entities.Entity

interface CollisionResolver {

    fun collide(entityA: Entity, entityB: Entity): Boolean {
        val boxA = entityA.get(BoundingBoxComponent::class)
        val boxB = entityB.get(BoundingBoxComponent::class)
        return collide(boxA, boxB)
    }

    fun collide(boxA: BoundingBoxComponent, boxB: BoundingBoxComponent): Boolean
}
