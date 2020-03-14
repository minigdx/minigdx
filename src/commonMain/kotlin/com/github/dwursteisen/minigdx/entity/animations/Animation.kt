package com.github.dwursteisen.minigdx.entity.animations

import kotlin.math.max

class Animation(
    val duration: Float,
    val keyFrames: Array<KeyFrame>
) {

    fun getOnionFrames(keyframe: Float): Pair<KeyFrame, KeyFrame> {
        var previous = keyFrames.first()
        var current = keyFrames.first()
        for (index in keyFrames.indices) {
            if (keyFrames[index].keyframe > keyframe) {
                current = keyFrames[index]
                previous = keyFrames[max(0, index - 1)]
                return previous to current
            }
        }
        return previous to current
    }
}

/**
 * Key frame of an animation of one property.
 *
 */
data class KeyFrame(
    var keyframe: Float = 0f,
    val pose: Armature
)
