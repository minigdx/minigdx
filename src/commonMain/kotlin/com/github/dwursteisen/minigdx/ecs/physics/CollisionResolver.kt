package com.github.dwursteisen.minigdx.ecs.physics

import com.curiouscreature.kotlin.math.Mat4
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox

interface CollisionResolver {

    fun collide(entityA: BoundingBox, positionA: Mat4, entityB: BoundingBox, positionB: Mat4): Boolean
}
