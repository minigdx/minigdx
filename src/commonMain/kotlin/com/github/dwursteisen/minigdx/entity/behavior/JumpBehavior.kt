package com.github.dwursteisen.minigdx.entity.behavior

import com.github.dwursteisen.minigdx.Seconds

class JumpBehavior(
    val charge: Float,
    val gravity: Float,
    val isJumping: (delta: Seconds) -> Boolean,
    val currentPosition: (delta: Seconds) -> Float,
    val groundPosition: (delta: Seconds) -> Float
) : Behaviour {

    var dy: Float = 0f
        private set

    var grounded: Boolean = true
        private set

    fun update(delta: Seconds) {
        if (isJumping(delta) && grounded) {
            grounded = false
            dy = charge
        }

        if (!grounded) {
            dy += gravity * delta
            if (currentPosition(delta) + dy < groundPosition(delta)) {
                grounded = true
            }
        } else {
            dy = 0f
        }
    }
}
