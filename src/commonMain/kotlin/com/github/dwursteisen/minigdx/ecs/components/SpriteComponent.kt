package com.github.dwursteisen.minigdx.ecs.components

import com.dwursteisen.minigdx.scene.api.model.UV
import com.dwursteisen.minigdx.scene.api.sprite.SpriteAnimation

class SpriteComponent(
    val animations: Map<String, SpriteAnimation> = emptyMap(),
    val uvs: List<UV>,
    var currentFrame: Int = -1,
    var frameDuration: Float = 0f,
    var currentAnimation: SpriteAnimation? = animations.values.firstOrNull()
    // TODO: Add a field with the related MeshPrimitive
) : Component {

    /**
     * Change the current animation to the animation with the [name].
     */
    fun switchToAnimation(name: String) {
        val newAnimation = animations[name] ?: return
        currentAnimation = newAnimation
        currentFrame = -1
        frameDuration = -1f
    }
}
