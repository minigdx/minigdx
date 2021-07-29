package com.github.dwursteisen.minigdx.graph

import com.github.dwursteisen.minigdx.ecs.components.CameraComponent

class CameraNode(
    val type: CameraComponent.Type,

    val far: Float,
    val near: Float = 0f,

    val fov: Float = 0f,
    val scale: Float = 0f,
)
