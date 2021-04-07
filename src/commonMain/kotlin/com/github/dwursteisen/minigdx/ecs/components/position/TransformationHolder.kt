package com.github.dwursteisen.minigdx.ecs.components.position

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.Quaternion
import com.curiouscreature.kotlin.math.normalize
import com.curiouscreature.kotlin.math.rotation
import com.curiouscreature.kotlin.math.translation

class TransformationHolder(
    translation: Mat4 = Mat4.identity(),
    rotation: Mat4 = Mat4.identity(),
    scale: Mat4 = Mat4.identity()
) {

    private var combined: Mat4 = Mat4.identity()

    var transalation: Mat4 = translation(translation)
        set(value) {
            field = translation(value)
            combined = updateTransformation()
        }

    var rotation: Quaternion = normalize(Quaternion.from(rotation(rotation)))
        set(value) {
            field = value
            combined = updateTransformation()
        }

    var scale: Mat4 = com.curiouscreature.kotlin.math.scale(scale)
        set(value) {
            field = com.curiouscreature.kotlin.math.scale(value)
            combined = updateTransformation()
        }

    var transformation: Mat4
        get() = combined
        set(value) {
            transalation = translation(value)
            rotation = normalize(
                Quaternion.from(
                    rotation(
                        Float3(
                            value.rotation.x,
                            value.rotation.y,
                            value.rotation.z
                        )
                    )
                )
            )
            scale = com.curiouscreature.kotlin.math.scale(value)
            combined = updateTransformation()
        }

    init {
        combined = updateTransformation()
    }

    private fun updateTransformation(): Mat4 {
        return transalation * Mat4.from(rotation) * scale
    }

    companion object {
        val identity = TransformationHolder()
    }
}
