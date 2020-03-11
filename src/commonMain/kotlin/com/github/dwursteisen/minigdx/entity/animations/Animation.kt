package com.github.dwursteisen.minigdx.entity.animations

import com.github.dwursteisen.minigdx.math.Vector3
import kotlin.math.max
import kotlin.math.min

class Animation(
    val duration: Float,
    private val keyFrames: Array<KeyFrame>
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

fun interpolate(a: Vector3, b: Vector3, alpha: Float): Vector3 {
    val x = (max(a.x, b.x) - min(a.x, b.x)) / alpha
    val y = (max(a.y, b.y) - min(a.y, b.y)) / alpha
    val z = (max(a.z, b.z) - min(a.z, b.z)) / alpha

    return Vector3(min(a.x, b.x) + x, min(a.y, b.y) + y, min(a.z, b.z) + z)
}
