package com.github.dwursteisen.minigdx.entity.animations

import com.curiouscreature.kotlin.math.Quaternion
import com.github.dwursteisen.minigdx.math.Vector3
import kotlin.math.max
import kotlin.math.min

class Animation(
    val duration: Float,
    private val transformations: Array<KeyFrame<Vector3>>,
    private val rotations: Array<KeyFrame<Quaternion>>
) {

    fun getOnionFrames(keyframe: Float): Pair<Frame, Frame> {
        val (previousTransformation, currentTransformation) = getPreviousCurrentFrame(keyframe, transformations)
        val (previousRotation, currentRotation) = getPreviousCurrentFrame(keyframe, rotations)

        return Frame(
            transformation = previousTransformation,
            rotation = previousRotation
        ) to Frame(
            transformation = currentTransformation,
            rotation = currentRotation
        )
    }

    private fun <T> getPreviousCurrentFrame(
        keyframe: Float,
        frames: Array<KeyFrame<T>>
    ): Pair<KeyFrame<T>, KeyFrame<T>> {
        var previous = frames.first()
        var current = frames.first()
        for (index in frames.indices) {
            if (frames[index].keyframe > keyframe) {
                current = frames[index]
                previous = frames[max(0, index - 1)]
            }
        }

        return previous to current
    }
}

class Frame(
    val transformation: KeyFrame<Vector3> = KeyFrame(),
    val rotation: KeyFrame<Quaternion> = KeyFrame()
)

/**
 * Key frame of an animation of one property.
 *
 */
class KeyFrame<T>(
    var keyframe: Float = 0f,
    val joints: MutableMap<String, T> = mutableMapOf()
)

fun interpolate(a: Vector3, b: Vector3, alpha: Float): Vector3 {
    val x = (max(a.x, b.x) - min(a.x, b.x)) / alpha
    val y = (max(a.y, b.y) - min(a.y, b.y)) / alpha
    val z = (max(a.z, b.z) - min(a.z, b.z)) / alpha

    return Vector3(min(a.x, b.x) + x, min(a.y, b.y) + y, min(a.z, b.z) + z)
}
