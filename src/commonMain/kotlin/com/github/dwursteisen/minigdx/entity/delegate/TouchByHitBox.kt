package com.github.dwursteisen.minigdx.entity.delegate

import com.github.dwursteisen.minigdx.entity.CanMove
import com.github.dwursteisen.minigdx.entity.CanTouchByHitBox
import com.github.dwursteisen.minigdx.entity.HitBox
import com.github.dwursteisen.minigdx.math.Vector2

class TouchByHitBox(
    override val hitbox: HitBox,
    val origin: CanMove
) : CanTouchByHitBox {

    private val position: Vector2 = Vector2(0f, 0f)

    override val hitboxPosition: Vector2
        get() {
            position.x = origin.position.x
            position.y = origin.position.y
            return position
        }

    override fun hit(other: CanTouchByHitBox): Boolean {
        val ax1 = this.hitboxPosition.x
        val ax2 = ax1 + hitbox.width

        val ay1 = this.hitboxPosition.y
        val ay2 = ay1 + hitbox.height

        val bx1 = other.hitboxPosition.x
        val bx2 = bx1 + other.hitbox.width

        val by1 = other.hitboxPosition.y
        val by2 = by1 + other.hitbox.height

        return ((ax1..ax2).contains(bx1) || (ax1..ax2).contains(bx2)) &&
                (ay1..ay2).contains(by1) || (ay1..ay2).contains(by2)
    }
}
