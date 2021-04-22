package com.github.dwursteisen.minigdx.ecs.components

import com.curiouscreature.kotlin.math.Mat4
import com.github.dwursteisen.minigdx.ecs.entities.Entity

class Camera(
    var projection: Mat4,
    var lookAt: Entity? = null
) : Component
