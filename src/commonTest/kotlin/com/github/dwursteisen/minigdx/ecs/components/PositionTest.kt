package com.github.dwursteisen.minigdx.ecs.components

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.fail

fun assertEquals(
    expected: Float,
    actual: Float,
    delta: Float = 0.001f,
    message: String = "$expected != $actual (𝝙 $delta)"
) {
    if (abs(expected - actual) > delta) {
        fail(message)
    }
}

class PositionTest {

    @Test
    fun setRotation_set_rotationX() {
        val rotation = Position().setRotationX(90f).transformation.rotation
        assertEquals(90f, rotation.x)
    }

    @Test
    fun setRotation_set_rotationY() {
        val rotation = Position().setRotationY(90f).transformation.rotation
        assertEquals(90f, rotation.y)
    }

    @Test
    fun setRotation_set_rotationZ() {
        val rotation = Position().setRotationZ(90f).transformation.rotation
        assertEquals(90f, rotation.z)
    }

    @Test
    fun translate_translate() {
        val translate = Position().translate(3, 4, 5).transformation.translation
        assertEquals(3f, translate.x)
        assertEquals(4f, translate.y)
        assertEquals(5f, translate.z)
    }

    @Test
    fun translate_setTranslate() {
        val translate = Position().translate(3, 4, 5).setTranslate(1, 2, 3).transformation.translation
        assertEquals(1f, translate.x)
        assertEquals(2f, translate.y)
        assertEquals(3f, translate.z)
    }
}
