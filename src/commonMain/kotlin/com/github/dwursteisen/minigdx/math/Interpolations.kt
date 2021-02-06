package com.github.dwursteisen.minigdx.math

import com.github.dwursteisen.minigdx.Seconds
import kotlin.math.pow

object Interpolations {
    fun lerp(target: Float, current: Float, step: Float = 0.9f): Float {
        return target + step * (current - target)
    }

    fun lerp(target: Float, current: Float, step: Float = 0.9f, deltaTime: Seconds): Float {
        return lerp(target, current, 1 - step.pow(deltaTime))
    }
}
