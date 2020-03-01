package com.github.dwursteisen.minigdx.entity

import kotlin.random.Random

object Colors {

    private val random = Random(0)

    fun random(alpha: Float = 1.0f): Color {
        return Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), alpha)
    }

    val WHITE = Color(1, 1, 1, 1)
    val RED = Color(1, 0, 0, 1)
    val GREEN = Color(0, 1, 0, 1)
    val BLUE = Color(0, 0, 1, 1)
    val BLACK = Color(0, 0, 0, 1)
}
