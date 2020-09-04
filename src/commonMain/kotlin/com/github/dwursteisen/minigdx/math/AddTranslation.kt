package com.github.dwursteisen.minigdx.math

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.rotation
import com.curiouscreature.kotlin.math.scale
import com.curiouscreature.kotlin.math.translation
import com.github.dwursteisen.minigdx.ecs.components.Position

sealed class OriginTransformation {
    abstract fun get(position: Position): Mat4
}

object Global : OriginTransformation() {
    override fun get(position: Position) = Mat4.identity()
}

object Local : OriginTransformation() {
    override fun get(position: Position): Mat4 = translation(position.transformation.position)
}

class FixedPoint(val x: Float, val y: Float, val z: Float) : OriginTransformation() {
    override fun get(position: Position): Mat4 {
        return translation(Float3(x, y, z))
    }
}

sealed class ObjectTransformation {

    abstract fun execute(position: Position)
}

class AddTranslation(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f,
    var origin: OriginTransformation = Global
) : ObjectTransformation() {

    override fun execute(position: Position) {
        when (val ol = origin) {
            Global, is FixedPoint -> position.transformation = translation(Float3(x, y, z)) * position.transformation
            Local -> position.transformation = position.transformation * translation(Float3(x, y, z))
        }
    }
}

class SetTranslation(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f,
    var origin: OriginTransformation = Global
) : ObjectTransformation() {
    override fun execute(position: Position) {
        when (val ol = origin) {
            Global -> position.transformation = position.transformation * translation(Float3(
                -position.transformation.translation.x,
                -position.transformation.translation.y,
                -position.transformation.translation.z
            )) * translation(Float3(x, y, z))
            Local -> position.transformation = position.transformation * translation(Float3(x, y, z))
            is FixedPoint -> {
                val rotation = position.transformation.rotation
                val scale = position.transformation.scale
                position.transformation = translation(Float3(ol.x + x, ol.y + y, ol.z + z)) * rotation(rotation) * scale(scale)
            }
        }
    }
}
