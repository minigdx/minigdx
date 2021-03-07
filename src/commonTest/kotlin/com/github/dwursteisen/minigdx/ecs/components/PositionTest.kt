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
    fun setRotation_setLocalRotationX() {
        val position = Position()
        val rotation = position.setLocalRotation(x = 90f).transformation.rotation
        assertEquals(90f, rotation.x, 0.1f)
        assertEquals(90f, position.rotation.x, 0.1f)
    }

    @Test
    fun setRotation_setLocalRotationY() {
        val position = Position()
        val rotation = position.setLocalRotation(y = 90f).transformation.rotation
        assertEquals(90f, rotation.y, 0.1f)
        assertEquals(90f, position.rotation.y, 0.1f)
    }

    @Test
    fun setRotation_setLocalRotationZ() {
        val position = Position()
        val rotation = position.setLocalRotation(z = 90f).transformation.rotation
        assertEquals(90f, rotation.z, 0.1f)
        assertEquals(90f, position.rotation.z, 0.1f)
    }

    @Test
    fun translate_addLocalTranslation() {
        val position = Position()
        val translate = position.addLocalTranslation(3, 4, 5, 1f).transformation.translation
        assertEquals(3f, translate.x)
        assertEquals(4f, translate.y)
        assertEquals(5f, translate.z)
        assertEquals(3f, position.translation.x)
        assertEquals(4f, position.translation.y)
        assertEquals(5f, position.translation.z)
    }

    @Test
    fun translate_setGlobalTranslation() {
        val position = Position()
        val translate = position
            .addImmediateLocalTranslation(7, 8, 9)
            .setGlobalTranslation(1, 2, 3)
            .transformation.translation
        assertEquals(1f, translate.x)
        assertEquals(2f, translate.y)
        assertEquals(3f, translate.z)
        assertEquals(1f, position.translation.x)
        assertEquals(2f, position.translation.y)
        assertEquals(3f, position.translation.z)
    }

    @Test
    fun scale_setScale() {
        val position = Position()
        position.setScale(1f, 2f, 3f)
        assertEquals(1f, position.scale.x)
        assertEquals(2f, position.scale.y)
        assertEquals(3f, position.scale.z)
        position.setScale(3f, 2f, 1f)
        assertEquals(3f, position.scale.x)
        assertEquals(2f, position.scale.y)
        assertEquals(1f, position.scale.z)
    }

    @Test
    fun scale_addScale() {
        val position = Position()
        position.addScale(1f, 2f, 3f, 1f)
        assertEquals(1f, position.scale.x)
        assertEquals(2f, position.scale.y)
        assertEquals(3f, position.scale.z)
        position.addScale(3f, 2f, 1f, 1f)
        assertEquals(4f, position.scale.x)
        assertEquals(4f, position.scale.y)
        assertEquals(4f, position.scale.z)
    }
}
