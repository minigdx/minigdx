package com.github.dwursteisen.minigdx.entity

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.inverse
import com.curiouscreature.kotlin.math.perspective
import com.curiouscreature.kotlin.math.transpose
import com.github.dwursteisen.minigdx.entity.delegate.Movable
import com.github.dwursteisen.minigdx.gl
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.dwursteisen.minigdx.shaders.ShaderProgram

class Camera(
    var projectionMatrix: Mat4
) : Entity, CanMove by Movable() {

    fun draw(program: ShaderProgram) {
        val normalMatrix = transpose(inverse(modelMatrix))

        gl.uniformMatrix4fv(program.getUniform("uProjectionMatrix"), false, projectionMatrix)
        gl.uniformMatrix4fv(program.getUniform("uViewMatrix"), false, modelMatrix)
        gl.uniformMatrix4fv(program.getUniform("uNormalMatrix"), false, normalMatrix)
    }

    fun lookAt(x: Number, y: Number, z: Number) = lookAt(eye = Vector3(x, y, z))

    fun lookAt(
        eye: Vector3 = position,
        target: Vector3 = Vector3(),
        up: Vector3 = rotation
    ) {
        projectionMatrix = com.curiouscreature.kotlin.math.lookAt(
            eye = Float3(eye.x, eye.y, eye.z),
            target = Float3(target.x, target.y, target.z),
            up = Float3(up.x, up.y, up.z)
        )
    }

    companion object {

        fun create(fov: Number, aspect: Number, near: Number, far: Number): Camera {
            // en radians
            val projectionMatrix = perspective(
                fov = fov.toFloat(),
                aspect = aspect.toFloat(),
                near = near.toFloat(),
                far = far.toFloat()
            )
            return Camera(
                projectionMatrix = projectionMatrix
            )
        }
    }
}
