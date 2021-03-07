package com.github.dwursteisen.minigdx.math

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.Quaternion
import com.curiouscreature.kotlin.math.scale
import com.curiouscreature.kotlin.math.translation
import com.github.dwursteisen.minigdx.Seconds
import kotlin.math.pow

object Interpolations {
    fun lerp(target: Float, current: Float, step: Float = 0.9f): Float {
        return target + step * (current - target)
    }

    fun lerp(target: Float, current: Float, step: Float = 0.9f, deltaTime: Seconds): Float {
        return lerp(target, current, 1 - step.pow(deltaTime))
    }

    fun lerp(target: Mat4, current: Mat4, step: Float = 0.9f): Mat4 {
        val translationTarget = target.translation
        val rotationTarget = Quaternion.from(target)
        val scaleTarget = target.scale

        val translationCurrent = current.translation
        val rotationCurrent = Quaternion.from(current)
        val scaleCurrent = current.scale

        val t = translation(
            Float3(lerp(translationTarget.x, translationCurrent.x, step),
                lerp(translationTarget.y, translationCurrent.y, step),
                lerp(translationTarget.z, translationCurrent.z, step)
            )
        )
        val r = Quaternion(
            x = lerp(rotationTarget.x, rotationCurrent.x, step),
            y = lerp(rotationTarget.y, rotationCurrent.y, step),
            z = lerp(rotationTarget.z, rotationCurrent.z, step),
            w = lerp(rotationTarget.w, rotationCurrent.w, step)
        )
        val s = scale(
            Float3(
                lerp(scaleTarget.x, scaleCurrent.x, step),
                lerp(scaleTarget.y, scaleCurrent.y, step),
                lerp(scaleTarget.z, scaleCurrent.z, step)
            )
        )

        return t * Mat4.from(r) * s
    }

    fun lerp(target: Mat4, current: Mat4, step: Float = 0.9f, deltaTime: Seconds): Mat4 {
        return lerp(target, current, 1 - step.pow(deltaTime))
    }
}
