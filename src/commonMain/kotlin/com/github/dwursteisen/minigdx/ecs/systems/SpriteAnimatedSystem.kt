package com.github.dwursteisen.minigdx.ecs.systems

import com.dwursteisen.minigdx.scene.api.sprite.SpriteAnimation
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.components.SpriteAnimated
import com.github.dwursteisen.minigdx.ecs.components.gl.MeshPrimitive
import com.github.dwursteisen.minigdx.ecs.entities.Entity

class SpriteAnimatedSystem : System(EntityQuery(SpriteAnimated::class)) {

    override fun update(delta: Seconds, entity: Entity) {
        val sprite = entity.get(SpriteAnimated::class)
        val spritePrimitive = entity.get(MeshPrimitive::class)
        sprite.frameDuration -= delta
        if (sprite.frameDuration <= 0f) {
            sprite.currentFrame = advanceToNextFrame(sprite.currentFrame, sprite.currentAnimation)
            val frame = sprite.currentAnimation.frames[sprite.currentFrame]
            sprite.frameDuration = frame.duration
            spritePrimitive.isDirty = true
            spritePrimitive.isUVDirty = true

            // FIXME: prendre en compte l' annimation courante
            // TODO: quick fix to get UVs in the same order than the model.
            val a = sprite.uvs[frame.uvIndex]
            val b = sprite.uvs[frame.uvIndex + 1]
            val c = sprite.uvs[frame.uvIndex + 2]
            val d = sprite.uvs[frame.uvIndex + 3]

            val uvs = arrayOf(a, d, b, c)

            spritePrimitive.primitive.vertices.forEachIndexed { index, vertex ->
                vertex.uv.x = uvs[index].x
                vertex.uv.y = uvs[index].y
            }
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
