package com.github.dwursteisen.minigdx.entity.animations

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.Quaternion
import com.github.dwursteisen.minigdx.math.Vector3

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
        // FIXME: check
        val (previousFrame, nextFrame) = currentAnimation.getOnionFrames(keyframe)

        val blend = (keyframe - previousFrame.keyframe) / (keyframe - nextFrame.keyframe)

        referencePose.traverse { join ->
            val joinId = join.id
            val prec = previousFrame.pose[joinId].localBindTransformation
            val next = nextFrame.pose[joinId].localBindTransformation

            val quaternionPrec = Quaternion.from(prec)
            val quaternionNext = Quaternion.from(next)
            val quarternionCurrent = com.curiouscreature.kotlin.math.interpolate(quaternionPrec, quaternionNext, blend)

            val positionPrec = prec.position.let { Vector3(it.x, it.y, it.z) }
            val positionNext = next.position.let { Vector3(it.x, it.y, it.z) }
            val positionCurrent = interpolate(positionPrec, positionNext, blend)

            val from = Mat4.from(quarternionCurrent)

            from.position = Float3(positionCurrent.x, positionCurrent.y, positionCurrent.z)

            val parent = join.parent?.id?.let {
                currentPose[it].globalInverseBindTransformation
            } ?: Mat4.identity()

            currentPose[joinId].globalInverseBindTransformation = from * parent
        }
    }
}
