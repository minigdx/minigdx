package com.github.dwursteisen.minigdx.ecs.physics

import com.curiouscreature.kotlin.math.Mat4
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.ecs.entities.Entity

interface CollisionResolver {

    fun collide(entityA: Entity, entityB: Entity): Boolean {
        val boxA = entityA.get(BoundingBox::class)
        val positionA = entityA.get(Position::class).combinedTransformation
        val boxB = entityB.get(BoundingBox::class)
        val positionB = entityB.get(Position::class).combinedTransformation
        return collide(boxA, positionA, boxB, positionB)
    }

    fun collide(
        entityA: Entity,
        positionA: Mat4 = entityA.get(Position::class).combinedTransformation,
        entityB: Entity,
        positionB: Mat4 = entityB.get(Position::class).combinedTransformation
    ): Boolean {
        val boxA = entityA.get(BoundingBox::class)
        val boxB = entityB.get(BoundingBox::class)
        return collide(boxA, positionA, boxB, positionB)
    }

    fun collide(entityA: BoundingBox, positionA: Mat4, entityB: BoundingBox, positionB: Mat4): Boolean
}
