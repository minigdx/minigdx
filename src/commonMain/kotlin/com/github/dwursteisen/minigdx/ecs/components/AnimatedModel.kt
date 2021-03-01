package com.github.dwursteisen.minigdx.ecs.components

import com.curiouscreature.kotlin.math.Mat4
import com.dwursteisen.minigdx.scene.api.armature.Animation
import com.dwursteisen.minigdx.scene.api.armature.Armature
import com.dwursteisen.minigdx.scene.api.armature.Frame

class AnimatedModel(
    var animation: List<Frame>,
    val animations: Map<String, Animation> = emptyMap(),
    val referencePose: Armature,
    val currentPose: Array<Mat4> = Array(40) { Mat4.identity() },
    var time: Float,
    var duration: Float,
    var loop: Long = 0
) : Component {

    fun switchAnimation(animationName: String) {
        val animation = animations[animationName] ?: return
        this.animation = animation.frames
        time = 0f
        loop = 0
        duration = animation.frames.maxBy { it.time }?.time ?: 0f
    }

    fun currentAnimationFinished() = loop > 0
}
