package com.github.dwursteisen.minigdx.entity.models

import com.curiouscreature.kotlin.math.normalize
import com.github.dwursteisen.minigdx.entity.CanDraw
import com.github.dwursteisen.minigdx.entity.CanMove
import com.github.dwursteisen.minigdx.entity.delegate.Movable
import com.github.dwursteisen.minigdx.entity.primitives.Color
import com.github.dwursteisen.minigdx.gl
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.dwursteisen.minigdx.shaders.ShaderProgram

class Light : CanDraw, CanMove by Movable() {

    val power: Vector3 = Vector3(0.6f, 0.6f, 0.6f)

    val color: Color = Color(0.5f, 0.5f, 0.75f, 1.0f)

    init {
        setTranslate(x = 0f, z = 5f)
        // setRotationX(90f)
    }

    override fun draw(shader: ShaderProgram) {
        val direction = normalize(modelMatrix.translation)

        gl.uniform3f(shader.getUniform("uLightAmbient"), power.x, power.y, power.z)
        gl.uniform3f(shader.getUniform("uLightColor"), color.r, color.g, color.b)
        gl.uniform3f(shader.getUniform("uLightDirection"), direction.x, direction.y, direction.z)
    }
}
