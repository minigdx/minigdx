package com.github.dwursteisen.minigdx.entity.models

import com.curiouscreature.kotlin.math.Mat4
import com.github.dwursteisen.minigdx.entity.CanMove
import com.github.dwursteisen.minigdx.entity.Entity
import com.github.dwursteisen.minigdx.entity.delegate.Movable
import com.github.dwursteisen.minigdx.gl
import com.github.dwursteisen.minigdx.shaders.ShaderProgram

class Camera2D(
    var projectionMatrix: Mat4
) : Entity, CanMove by Movable() {

    fun draw(program: ShaderProgram) {
        gl.uniformMatrix4fv(program.getUniform("uProjectionMatrix"), false, projectionMatrix)
        gl.uniformMatrix4fv(program.getUniform("uViewMatrix"), false, modelMatrix)
    }

    companion object {
        fun orthographic(width: Float, height: Float): Camera2D {
            val projectionMatrix = com.curiouscreature.kotlin.math.ortho(
                l = 0f, r = width,
                b = 0f, t = height,
                n = 0f, f = 10f
            )
            return Camera2D(
                projectionMatrix = projectionMatrix
            )
        }
    }
}
