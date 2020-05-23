package com.github.dwursteisen.minigdx.entity.animations

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.entity.CanAnimate
import com.github.dwursteisen.minigdx.entity.CanDraw
import com.github.dwursteisen.minigdx.entity.CanMove
import com.github.dwursteisen.minigdx.entity.Entity
import com.github.dwursteisen.minigdx.entity.delegate.Drawable
import com.github.dwursteisen.minigdx.log
import com.github.dwursteisen.minigdx.shaders.ShaderProgram

class AnimatedModel(
    animation: Animation,
    model: Drawable,
    val animations: Map<String, Animation>,
    val armature: Armature = model.pose!!,
    var drawJoint: Boolean = false
) : Entity, CanDraw, CanMove by model.mesh,
    CanAnimate {

    private val animator = Animator(
        currentAnimation = animation,
        referencePose = armature
    )

    val animationsName
        get() = animations.keys

    private val drawable = Drawable(
        model.mesh,
        animator.currentPose
    )

    private val jointMeshs = JointMesh.of(
        referencePose = armature,
        currentPose = animator.currentPose
    )

    init {
        log.info("MODEL") {
            """New animated model created using:
               - '${animation.keyFrames.size}' key frames
               - '${armature.allJoints.size}' joints
               - drawing joint: '$drawJoint' 
            """
        }
    }

    override fun update(delta: Seconds) {
        animator.update(delta)
    }

    override fun draw(shader: ShaderProgram) {
        drawable.draw(shader)

        if (drawJoint) {
            jointMeshs.draw(shader)
        }
    }

    override fun switchAnimation(animationName: String) {
        animator.currentAnimation = animations[animationName] ?: throw IllegalArgumentException("Animation '$animationName' unknown!")
        animator.animationTime = 0f // reset animation.
    }
}
