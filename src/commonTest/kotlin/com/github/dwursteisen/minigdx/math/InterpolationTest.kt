package com.github.dwursteisen.minigdx.math

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.normalize
import com.curiouscreature.kotlin.math.rotation
import com.curiouscreature.kotlin.math.scale
import com.curiouscreature.kotlin.math.translation
import com.github.dwursteisen.minigdx.math.Interpolations.lerp
import kotlin.test.Test

class InterpolationTest {

    private fun assertEquals(expected: Mat4, result: Mat4) {
        expected.asGLArray().zip(result.asGLArray()).forEach { (a, b) ->
            com.github.dwursteisen.minigdx.ecs.components.assertEquals(a, b)
        }
    }

    @Test
    fun matrix_lerp_interpolation_idt() {
        val idt = Mat4.identity()
        val result = lerp(idt, idt)
        assertEquals(idt, result)
    }

    @Test
    fun matrix_lerp_interpolation_only_transaction() {
        val idt = Mat4.identity()
        val target = translation(Float3(1f, 1f, 1f))
        var result = idt
        (0..100).forEach {
            result = lerp(target, result)
        }
        assertEquals(target, result)
    }

    @Test
    fun matrix_lerp_interpolation_only_rotation() {
        val idt = Mat4.identity()
        val target = rotation(normalize(Float3(90f, 180f, 45f)))
        var result = idt
        (0..100).forEach {
            result = lerp(target, result)
        }
        assertEquals(target, result)
    }

    @Test
    fun matrix_lerp_interpolation_only_scaled() {
        val idt = Mat4.identity()
        val target = scale(Float3(5f, 6f, 7f))
        var result = idt
        (0..100).forEach {
            result = lerp(target, result)
        }
        assertEquals(target, result)
    }
}
