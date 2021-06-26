package com.github.dwursteisen.minigdx.ecs.components

import com.curiouscreature.kotlin.math.Mat4
import com.dwursteisen.minigdx.scene.api.armature.Frame
import com.github.dwursteisen.minigdx.graph.AnimatedModel

class AnimatedComponent(
    val animatedModel: AnimatedModel,
    var currentAnimation: List<Frame> = emptyList(),
    /**
     * Current pose computed regarding the current animation and the time elapsed in this animation.
     */
    val currentPose: Array<Mat4> = Array(100) { Mat4.identity() },
    /**
     * Time elapsed in the current animation
     */
    var time: Float = 0f,
    /**
     * Duration of the current animation
     */
    var duration: Float = 0f,
    /**
     * Number of time the current animation has been played
     */
    var loop: Long = 0,
    /**
     * Is the current animation paused?
     */
    val pause: Boolean = false,
    /**
     * Is the animated model hidden?
     */
    val hidden: Boolean = false
) : Component {

    fun switchAnimation(animationName: String) {
        val animation = animatedModel.animations[animationName] ?: return
        currentAnimation = animation.frames
        time = 0f
        loop = 0
        duration = animation.frames.maxByOrNull { it.time }?.time ?: 0f
    }

    fun currentAnimationFinished() = loop > 0
}
