package com.github.dwursteisen.minigdx.entity.animations

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.entity.CanDraw
import com.github.dwursteisen.minigdx.entity.Entity
import com.github.dwursteisen.minigdx.entity.Mesh
import com.github.dwursteisen.minigdx.shaders.ShaderProgram

class AnimatedModel(
    animation: Animation,
    private val mesh: Mesh,
    private val armature: Armature,
    private var drawJoin: Boolean = false
) : Entity, CanDraw {

    private val animator = Animator(currentAnimation = animation, referencePose = armature)

    private val jointMeshs: Map<JointId, JointMesh> = armature.allJoints.mapValues {
        JointMesh.of(it.value, animator.currentPose)
    }

    override fun update(delta: Seconds) {
        animator.update(delta)
    }

    override fun draw(shader: ShaderProgram) {
        // FIXME: passer l'ensemble des matrices de transformations au shader.
        //   Les vertex doivent choisir le joint dans l'ensemble des matrices par rapport à l'indice.
        //   Comment gérer le fait qu'il ne peut ne pas y avoir d'armature au model ? => tableau de join vide
        //   ou id = -1
        //  Beaucoup plus simple : passer un uniform avec 1 ou -1 -> -1 == pas d'armature
        // armature.rootJoint

        if (drawJoin) {
            jointMeshs.values.forEach {
                it.draw(shader)
            }
        }
    }
}
