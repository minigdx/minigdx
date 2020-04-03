package com.github.dwursteisen.minigdx.entity.animations

class Animator(
    var animationTime: Float = 0f,
    var currentAnimation: Animation,
    val referencePose: Armature,
    val currentPose: Armature = referencePose.copy()
) {

    fun update(delta: Float) {
        animationTime += delta

        if (animationTime > currentAnimation.duration) {
            // reset animation time to loop.
            animationTime %= currentAnimation.duration
        }

        generatePose(animationTime)
    }

    private fun generatePose(keyframe: Float) {
        val (previousFrame, nextFrame) = currentAnimation.getOnionFrames(keyframe)

        // TODO: create interpolation here
        previousFrame.pose.traverse {
        //    currentPose[it.id].localBindTransformation = it.localBindTransformation
        //    currentPose[it.id].globalBindTransformation = it.globalBindTransformation
        //    currentPose[it.id].globalInverseBindTransformation = it.globalInverseBindTransformation
            val animation = it.globalBindTransformation * referencePose[it.id].globalInverseBindTransformation
            currentPose[it.id].animationTransformation = animation
        }
    }
}
