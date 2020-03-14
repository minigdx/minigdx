package com.github.dwursteisen.minigdx.entity.animations

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.Quaternion
import com.curiouscreature.kotlin.math.interpolate
import com.github.dwursteisen.minigdx.log
import com.github.dwursteisen.minigdx.math.Vector3

private fun Vector3.toFloat3() = Float3(x, y, z)
private fun Float3.toVector3() = Vector3(x, y, z)

class Animator(
    var animationTime: Float = 0f,
    var currentAnimation: Animation,
    val referencePose: Armature,
    val currentPose: Armature = referencePose.copy(),
    var frame: Int = 0
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

            val localBindTransformation = interpolate(prec, next, blend)
            currentPose[joinId].localBindTransformation = localBindTransformation

            val parent = currentPose[join.parent?.id ?: 0].globalInverseBindTransformation
            currentPose[joinId].globalInverseBindTransformation = localBindTransformation * parent

            // FIXME: test to check where is the issue
            // currentPose[joinId].localBindTransformation = nextFrame.pose[joinId].localBindTransformation
            // currentPose[joinId].globalInverseBindTransformation = nextFrame.pose[joinId].globalInverseBindTransformation
            currentPose[joinId].localBindTransformation = currentAnimation.keyFrames[frame].pose[joinId].localBindTransformation
            currentPose[joinId].globalInverseBindTransformation = currentAnimation.keyFrames[frame].pose[joinId].localBindTransformation
        }
    }

    private fun interpolate(
        prec: Mat4,
        next: Mat4,
        blend: Float
    ): Mat4 {
        val quaternionPrec = Quaternion.from(prec)
        val quaternionNext = Quaternion.from(next)
        val quaternionCurrent = interpolate(quaternionPrec, quaternionNext, blend)

        val positionPrec = prec.position.let { Vector3(it.x, it.y, it.z) }
        val positionNext = next.position.let { Vector3(it.x, it.y, it.z) }
        val positionCurrent = interpolate(positionPrec.toFloat3(), positionNext.toFloat3(), blend).toVector3()

        val localBindTransformation = Mat4.from(quaternionCurrent)

        localBindTransformation.position = Float3(positionCurrent.x, positionCurrent.y, positionCurrent.z)
        return localBindTransformation
    }

    fun nextFrame() {
        frame = (frame + 1) % currentAnimation.keyFrames.size
        log.info("ANIMATOR") {
            "Advance to next frame: '$frame / ${currentAnimation.keyFrames.size}' frame"
        }
    }
}
