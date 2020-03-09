package com.github.dwursteisen.minigdx.entity.animations

class Animator(
    var animationTime: Float = 0f,
    var currentAnimation: Animation,
    val currentFrame: Frame = Frame()
) {

    fun update(delta: Float) {
        animationTime += animationTime

        if (animationTime > currentAnimation.duration) {
            // reset animation time to loop.
            animationTime %= currentAnimation.duration
        }

        generatePose(animationTime)
    }

    private fun generatePose(keyframe: Float) {
        val (previousFrame, currentFrame) = currentAnimation.getOnionFrames(keyframe)
        val alphaRotation = (keyframe - previousFrame.rotation.keyframe) / (keyframe - currentFrame.rotation.keyframe)

        val alphaTransformation =
            (keyframe - previousFrame.transformation.keyframe) / (keyframe - currentFrame.transformation.keyframe)

        currentFrame.rotation.keyframe = keyframe
        previousFrame.rotation.joints.keys.forEach { joinId ->
            val prec = previousFrame.rotation.joints.getValue(joinId)
            val current = currentFrame.rotation.joints.getValue(joinId)
            currentFrame.rotation.joints[joinId] =
                com.curiouscreature.kotlin.math.interpolate(prec, current, alphaRotation)
        }

        currentFrame.transformation.keyframe = keyframe
        previousFrame.transformation.joints.keys.forEach { joinId ->
            val prec = previousFrame.transformation.joints.getValue(joinId)
            val current = currentFrame.transformation.joints.getValue(joinId)
            currentFrame.transformation.joints[joinId] =
                interpolate(
                    prec,
                    current,
                    alphaTransformation
                )
        }
    }
}
