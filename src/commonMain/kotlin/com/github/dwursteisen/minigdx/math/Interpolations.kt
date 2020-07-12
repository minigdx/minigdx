package com.github.dwursteisen.minigdx.math

fun lerp(target: Float, current: Float, step: Float = 0.9f): Float {
    return target + step * (current - target)
}
