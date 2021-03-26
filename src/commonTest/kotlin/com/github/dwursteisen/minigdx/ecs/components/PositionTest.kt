package com.github.dwursteisen.minigdx.ecs.components

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.rotation
import com.curiouscreature.kotlin.math.translation
import com.github.dwursteisen.minigdx.math.Vector3
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
    fun rotation_constructor() {
        val position = Position(globalRotation = rotation(Float3(90f, 0f, 0f)))
        assertEquals(90f, Mat4.from(position.quaternion).rotation.x, 0.1f)
        assertEquals(90f, position.globalRotation.x, 0.1f)
        assertEquals(0f, position.localRotation.x, 0.1f)
        assertEquals(90f, position.rotation.x, 0.1f)
    }

    @Test
    fun rotation_translation_constructor() {
        val position = Position(globalRotation = translation(Float3(1f, 2f, 3f)) * rotation(Float3(90f, 0f, 0f)))
        assertEquals(90f, Mat4.from(position.quaternion).rotation.x, 0.1f)
        assertEquals(90f, position.globalRotation.x, 0.1f)
        assertEquals(0f, position.localRotation.x, 0.1f)
        assertEquals(90f, position.rotation.x, 0.1f)
    }

    @Test
    fun rotation_setGlobalRotation() {
        val position = Position().setGlobalRotation(x = 90f)
        assertEquals(90f, position.globalRotation.x, 0.1f)
        assertEquals(0f, position.localRotation.x, 0.1f)
        assertEquals(90f, position.rotation.x, 0.1f)
    }

    @Test
    fun setRotation_setLocalRotationX() {
        val position = Position()
        val rotation = position.setLocalRotation(x = 90f).rotation
        assertEquals(90f, rotation.x, 0.1f)
        assertEquals(90f, position.rotation.x, 0.1f)
    }

    @Test
    fun setRotation_setLocalRotationY() {
        val position = Position()
        val rotation = position.setLocalRotation(y = 90f).rotation
        assertEquals(90f, rotation.y, 0.1f)
        assertEquals(90f, position.rotation.y, 0.1f)
    }

    @Test
    fun setRotation_setLocalRotationZ() {
        val position = Position()
        val rotation = position.setLocalRotation(z = 90f).rotation
        assertEquals(90f, rotation.z, 0.1f)
        assertEquals(90f, position.rotation.z, 0.1f)
    }

    @Test
    fun translate_addLocalTranslation() {
        val position = Position()
        val translate = position.addLocalTranslation(3, 4, 5, 1f).translation
        assertEquals(3f, translate.x)
        assertEquals(4f, translate.y)
        assertEquals(5f, translate.z)
        assertEquals(3f, position.localTranslation.x)
        assertEquals(4f, position.localTranslation.y)
        assertEquals(5f, position.localTranslation.z)
        assertEquals(0f, position.globalTranslation.x)
        assertEquals(0f, position.globalTranslation.y)
        assertEquals(0f, position.globalTranslation.z)
    }

    @Test
    fun translate_setGlobalTranslation() {
        val position = Position()
        val translate = position
            .addImmediateLocalTranslation(7, 8, 9)
            .setGlobalTranslation(1, 2, 3)
            .translation

        assertEquals(8f, translate.x)
        assertEquals(10f, translate.y)
        assertEquals(12f, translate.z)
        assertEquals(7f, position.localTranslation.x)
        assertEquals(8f, position.localTranslation.y)
        assertEquals(9f, position.localTranslation.z)
        assertEquals(1f, position.globalTranslation.x)
        assertEquals(2f, position.globalTranslation.y)
        assertEquals(3f, position.globalTranslation.z)
    }

    @Test
    fun scale_setScale() {
        val position = Position()
        position.setGlobalScale(1f, 2f, 3f)
        assertEquals(1f, position.scale.x)
        assertEquals(2f, position.scale.y)
        assertEquals(3f, position.scale.z)
        position.setGlobalScale(3f, 2f, 1f)
        assertEquals(3f, position.scale.x)
        assertEquals(2f, position.scale.y)
        assertEquals(1f, position.scale.z)
    }

    @Test
    fun scale_addScale() {
        val position = Position()
        position.addGlobalScale(1f, 2f, 3f, 1f)
        assertEquals(2f, position.scale.x)
        assertEquals(3f, position.scale.y)
        assertEquals(4f, position.scale.z)
        position.addGlobalScale(3f, 2f, 1f, 1f)
        assertEquals(5f, position.scale.x)
        assertEquals(5f, position.scale.y)
        assertEquals(5f, position.scale.z)
    }

    @Test
    fun rotation_aroundGlobal() {
        val position = Position()
        position.setGlobalTranslation(x = 20)
        position.addGlobalRotationAround(Vector3(x = 10f), z = 90f)
        assertEquals(10f, position.translation.x)
        assertEquals(10f, position.translation.y)
        assertEquals(0f, position.translation.z)
        position.addGlobalRotationAround(Vector3(x = 10f), z = 90f)
        assertEquals(0f, position.translation.x)
        assertEquals(0f, position.translation.y)
        assertEquals(0f, position.translation.z)
        position.addGlobalRotationAround(Vector3(x = 10f), z = -180f)
        assertEquals(20f, position.translation.x)
        assertEquals(0f, position.translation.y)
        assertEquals(0f, position.translation.z)
    }

    @Test
    fun simulation_rollback() {
        val position = Position()
        val result: Any = position.simulation {
            addLocalScale(1.0f, 1.0f, 1.0f)
            rollback()
        }
        assertEquals(1f, position.scale.x)
    }

    @Test
    fun simulation_commit() {
        val position = Position()
        val result: Any = position.simulation {
            addLocalScale(1.0f, 1.0f, 1.0f)
            commit()
        }
        assertEquals(2f, position.scale.x)
    }
}
