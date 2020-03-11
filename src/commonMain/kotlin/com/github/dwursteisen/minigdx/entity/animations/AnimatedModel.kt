package com.github.dwursteisen.minigdx.entity.animations

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.entity.CanDraw
import com.github.dwursteisen.minigdx.entity.Entity
import com.github.dwursteisen.minigdx.entity.Mesh
import com.github.dwursteisen.minigdx.entity.delegate.Drawable
import com.github.dwursteisen.minigdx.graphics.Render
import com.github.dwursteisen.minigdx.shaders.ShaderProgram

class AnimatedModel(
    animation: Animation,
    private val mesh: Mesh,
    private val armature: Armature,
    private var drawJoin: Boolean = false
) : Entity, CanDraw {

    private val animator = Animator(currentAnimation = animation, referencePose = armature)
    private val drawable = Drawable(Render(mesh, animator.currentPose))

    private val jointMeshs: Map<JointId, JointMesh> = armature.allJoints.mapValues {
        JointMesh.of(it.value, animator.currentPose)
    }

    override fun update(delta: Seconds) {
        animator.update(delta)
    }

    override fun draw(shader: ShaderProgram) {
        drawable.draw(shader)

        if (drawJoin) {
            jointMeshs.values.forEach {
                it.draw(shader)
            }
        }
    }
}
