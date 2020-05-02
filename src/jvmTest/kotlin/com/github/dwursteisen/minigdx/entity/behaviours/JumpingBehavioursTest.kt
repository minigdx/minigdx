package com.github.dwursteisen.minigdx.entity.behaviours

import com.github.dwursteisen.minigdx.entity.behavior.JumpBehavior
import kotlin.test.assertEquals
import org.junit.Test

class JumpingBehavioursTest {

    private var isJumping: Boolean = false
    private var position: Float = 0f
    private var ground: Float = 0f

    private val jumpingBehavior = JumpBehavior(
        charge = 4f,
        gravity = -0.98f,
        isJumping = { _ -> isJumping },
        currentPosition = { _ -> position },
        groundPosition = { _ -> ground }
    )

    @Test
    fun jump() {
        isJumping = true
        jumpingBehavior.update(0f)
        assertEquals(4f, jumpingBehavior.dy)
        assertEquals(false, jumpingBehavior.grounded)
    }

    @Test
    fun jumpAndRelease() {
        isJumping = true
        position = 1000f
        jumpingBehavior.update(10f)
        assertEquals(-5.8f, jumpingBehavior.dy)
        assertEquals(false, jumpingBehavior.grounded)
    }

    @Test
    fun jumpAndTouchGround() {
        isJumping = true
        position = 4f
        jumpingBehavior.update(10f)
        assertEquals(-5.8f, jumpingBehavior.dy)
        assertEquals(true, jumpingBehavior.grounded)
    }
}
