package com.github.dwursteisen.minigdx.entity.models

import com.curiouscreature.kotlin.math.Mat4
import com.github.dwursteisen.minigdx.WorldResolution
import com.github.dwursteisen.minigdx.entity.CanMove
import com.github.dwursteisen.minigdx.entity.Entity
import com.github.dwursteisen.minigdx.entity.delegate.Movable
import com.github.dwursteisen.minigdx.gl
import com.github.dwursteisen.minigdx.shaders.ShaderProgram

class Camera2D(
    var projectionMatrix: Mat4,
    val width: Float,
    val height: Float
) : Entity, CanMove by Movable() {

    fun draw(program: ShaderProgram) {
        gl.uniform2f(program.getUniform("uResolution"), width, height)
        gl.uniformMatrix4fv(program.getUniform("uProjectionMatrix"), false, projectionMatrix)
        gl.uniformMatrix4fv(program.getUniform("uViewMatrix"), false, modelMatrix)
    }

    companion object {
        fun orthographic(world: WorldResolution): Camera2D = orthographic(
            world.width.toFloat(),
            world.height.toFloat()
        )

        fun orthographic(width: Float, height: Float): Camera2D {
            // Ortho limits should be in the clip space.
            val projectionMatrix = com.curiouscreature.kotlin.math.ortho(
                l = -1f, r = 1f,
                b = -1f * (width / height), t = 1f * (width / height),
                n = 0f, f = 1f
            )
            return Camera2D(
                projectionMatrix = projectionMatrix,
                width = width,
                height = height
            )
        }
    }
}
