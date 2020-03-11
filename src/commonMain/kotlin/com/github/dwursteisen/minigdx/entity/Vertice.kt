package com.github.dwursteisen.minigdx.entity

import com.github.dwursteisen.minigdx.math.Vector3

data class JointsIndex(
    val a: Int,
    val b: Int = -1,
    val c: Int = -1
)

data class Influence(
    val joinIds: JointsIndex,
    val weight: Vector3 = Vector3(1f, 0f, 0f)
)

data class Vertice(
    val position: Vector3,
    val normal: Vector3 = Vector3(0, 0, 0),
    val color: Color,
    val influence: Influence? = null
)
