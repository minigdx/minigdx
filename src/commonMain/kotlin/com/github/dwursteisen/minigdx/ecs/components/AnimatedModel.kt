package com.github.dwursteisen.minigdx.ecs.components

import com.curiouscreature.kotlin.math.Mat4
import com.dwursteisen.minigdx.scene.api.armature.Armature
import com.dwursteisen.minigdx.scene.api.armature.Frame

class AnimatedModel(
    var animation: List<Frame>,
    val referencePose: Armature,
    val currentPose: Array<Mat4> = Array(40) { Mat4.identity() },
    var time: Float,
    val duration: Float
) : Component
