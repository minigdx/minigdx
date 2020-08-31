package com.github.dwursteisen.minigdx.ecs.components

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.rotation
import com.curiouscreature.kotlin.math.translation
import com.github.dwursteisen.minigdx.math.AddTranslation
import com.github.dwursteisen.minigdx.math.Global
import com.github.dwursteisen.minigdx.math.Local
import com.github.dwursteisen.minigdx.math.SetTranslation
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
        assertEquals(90f, rotation.x, 0.1f)
    }

    @Test
    fun setRotation_set_rotationY() {
        val rotation = Position().setRotationY(90f).transformation.rotation
        assertEquals(90f, rotation.y, 0.1f)
    }

    @Test
    fun setRotation_set_rotationZ() {
        val rotation = Position().setRotationZ(90f).transformation.rotation
        assertEquals(-90f, rotation.z, 0.1f)
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

    @Test
    fun apply_it_apply_nothing() {
        val position = Position()
        position.apply(AddTranslation())
        assertEquals(0f, position.translation.x)
        assertEquals(0f, position.translation.y)
        assertEquals(0f, position.translation.z)
    }

    @Test
    fun apply_it_add_translation_with_global_origin() {
        val position = Position(transformation = translation(Float3(1f, 0f, 0f)) * rotation(Float3(0f, 0f, 1f), 90f))
        position.apply(AddTranslation(x = 1f, origin = Global))
        assertEquals(2f, position.transformation.translation.x)
        assertEquals(0f, position.transformation.translation.y)
        assertEquals(0f, position.transformation.translation.z)
    }

    @Test
    fun apply_it_add_translation_with_local_origin() {
        val position = Position(transformation = translation(Float3(1f, 0f, 0f)) * rotation(Float3(0f, 0f, 1f), 90f))
        position.apply(AddTranslation(x = 1f, origin = Local))
        assertEquals(1f, position.transformation.translation.x)
        assertEquals(1f, position.transformation.translation.y)
        assertEquals(0f, position.transformation.translation.z)
    }

    @Test
    fun apply_it_set_translation_with_global_origin() {
        val position = Position(transformation = translation(Float3(1f, 0f, 0f)) * rotation(Float3(0f, 0f, 1f), 90f))
        position.apply(SetTranslation(x = 1f, origin = Global))
        assertEquals(1f, position.transformation.translation.x)
        assertEquals(0f, position.transformation.translation.y)
        assertEquals(0f, position.transformation.translation.z)
    }

    @Test
    fun apply_it_set_translation_with_local_origin() {
        val position = Position(transformation = translation(Float3(1f, 0f, 0f)) * rotation(Float3(0f, 0f, 1f), 90f))
        position.apply(SetTranslation(x = 1f, origin = Local))
        assertEquals(1f, position.transformation.translation.x)
        assertEquals(1f, position.transformation.translation.y)
        assertEquals(0f, position.transformation.translation.z)
    }
}
