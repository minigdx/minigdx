package com.github.dwursteisen.minigdx.ecs.systems

import com.dwursteisen.minigdx.scene.api.sprite.SpriteAnimation
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.SpriteAnimated
import com.github.dwursteisen.minigdx.ecs.components.gl.SpritePrimitive
import com.github.dwursteisen.minigdx.ecs.entities.Entity

class SpriteAnimatedSystem : System(EntityQuery(SpriteAnimated::class)) {

    override fun update(delta: Seconds, entity: Entity) {
        val sprite = entity.get(SpriteAnimated::class)
        val spritePrimitive = entity.get(SpritePrimitive::class)
        sprite.frameDuration -= delta
        if (sprite.frameDuration <= 0f) {
            sprite.currentFrame = advanceToNextFrame(sprite.currentFrame, sprite.currentAnimation)
            sprite.frameDuration = sprite.currentAnimation.frames[sprite.currentFrame].duration
            spritePrimitive.isDirty = true
            val a = sprite.uvs[sprite.currentFrame * 4]
            val b = sprite.uvs[sprite.currentFrame * 4 + 1]
            val c = sprite.uvs[sprite.currentFrame * 4 + 2]
            val d = sprite.uvs[sprite.currentFrame * 4 + 3]

            spritePrimitive.uvs = floatArrayOf(b.x, 1f - b.y, c.x, 1f - c.y, a.x, 1f - a.y, d.x, 1f - d.y)
        }
    }

    private fun advanceToNextFrame(currentFrame: Int, animation: SpriteAnimation): Int {
        var nextFrame = currentFrame + 1
        if (nextFrame >= animation.frames.size) {
            nextFrame = 0
        }
        return nextFrame
    }
}
