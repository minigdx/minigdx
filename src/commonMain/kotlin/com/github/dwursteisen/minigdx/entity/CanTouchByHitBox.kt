package com.github.dwursteisen.minigdx.entity

import com.github.dwursteisen.minigdx.math.Vector2

data class HitBox(val width: Float, val height: Float)

interface CanTouchByHitBox {

    val hitbox: HitBox

    val hitboxPosition: Vector2

    fun hit(other: CanTouchByHitBox): Boolean
}
