package com.github.dwursteisen.minigdx.ecs.components

import com.dwursteisen.minigdx.scene.api.model.UV
import com.dwursteisen.minigdx.scene.api.sprite.SpriteAnimation

class SpriteAnimated(
    val animations: Map<String, SpriteAnimation>,
    val uvs: List<UV>,
    var currentFrame: Int = -1,
    var frameDuration: Float = 0f,
    var currentAnimation: SpriteAnimation = animations.values.first()
) : Component {

    fun switchToAnimation(name: String) {
        val newAnimation = animations[name]
        if (newAnimation != null) {
            currentAnimation = newAnimation
            currentFrame = 0
            frameDuration = currentAnimation.frames.firstOrNull()?.duration ?: 0f
        }
    }
}
