package com.github.dwursteisen.minigdx.ecs.systems

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.api.toMat4
import com.github.dwursteisen.minigdx.ecs.components.AnimatedComponent
import com.github.dwursteisen.minigdx.ecs.entities.Entity

class ArmatureUpdateSystem : System(
    EntityQuery(
        AnimatedComponent::class
    )
) {

    override fun update(delta: Seconds, entity: Entity) {
        entity.findAll(AnimatedComponent::class).forEach { component ->
            component.time += delta
            if (component.time > component.duration) {
                component.time = 0f
                component.loop++
            }

            val currentFrame = component.currentAnimation.lastOrNull { f -> f.time <= component.time } ?: component.currentAnimation.firstOrNull() ?: return@forEach

            (component.animatedModel.referencePose.joints.indices).forEach { index ->
                val globalTransformation = currentFrame.globalTransformations[index]
                val inverseGlobalTransformation = component.animatedModel.referencePose.joints[index].inverseGlobalTransformation
                component.currentPose[index] = globalTransformation.toMat4() * inverseGlobalTransformation.toMat4()
            }
        }
    }
}
