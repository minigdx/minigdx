package com.github.dwursteisen.minigdx.entity

import com.github.dwursteisen.minigdx.math.Vector3

data class Vertice(
    val position: Vector3,
    val normal: Vector3 = Vector3(0, 0, 0),
    val color: Color
)
