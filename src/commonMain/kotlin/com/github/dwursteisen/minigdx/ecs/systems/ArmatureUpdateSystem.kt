package com.github.dwursteisen.minigdx.ecs.systems

import com.curiouscreature.kotlin.math.Mat4
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.AnimatedModel
import com.github.dwursteisen.minigdx.ecs.entities.Entity

class ArmatureUpdateSystem : System(
    EntityQuery(
        AnimatedModel::class
    )
) {

    override fun update(delta: Seconds, entity: Entity) {
        entity.findAll(AnimatedModel::class).forEach {
            it.time += delta
            if (it.time > it.duration) {
                it.time = 0f
                it.loop++
            }

            val currentFrame = it.animation.lastOrNull { f -> f.time <= it.time } ?: it.animation.firstOrNull() ?: return@forEach

            (it.referencePose.joints.indices).forEach { index ->
                it.currentPose[index] = Mat4.fromColumnMajor(
                    *currentFrame.globalTransformations[index].matrix
                ) *
                    Mat4.fromColumnMajor(*it.referencePose.joints[index].inverseGlobalTransformation.matrix)
            }
        }
    }
}
