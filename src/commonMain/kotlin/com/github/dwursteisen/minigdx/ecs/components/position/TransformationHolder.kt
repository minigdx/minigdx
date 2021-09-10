package com.github.dwursteisen.minigdx.ecs.components.position

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.Quaternion
import com.curiouscreature.kotlin.math.rotation
import com.curiouscreature.kotlin.math.scale
import com.curiouscreature.kotlin.math.translation

class TransformationHolder(
    translation: Mat4 = Mat4.identity(),
    rotation: Mat4 = Mat4.identity(),
    scale: Mat4 = Mat4.identity()
) {

    private var combined: Mat4 = Mat4.identity()

    var translation: Mat4 = translation(translation)
        set(value) {
            field = translation(value)
            combined = updateTransformation()
        }

    var rotation: Quaternion = Quaternion.from(rotation)
        set(value) {
            field = value
            combined = updateTransformation()
        }

    var scale: Mat4 = scale(scale)
        set(value) {
            field = scale(value)
            combined = updateTransformation()
        }

    var transformation: Mat4
        get() = combined
        set(value) {
            translation = translation(value)
            rotation = Quaternion.from(
                rotation(
                    Float3(
                        value.rotation.x,
                        value.rotation.y,
                        value.rotation.z
                    )
                )
            )
            scale = scale(value)
            combined = value
        }

    init {
        combined = updateTransformation()
    }

    private fun updateTransformation(): Mat4 {
        return translation * Mat4.from(rotation) * scale
    }

    companion object {

        val identity = TransformationHolder()
    }
}
