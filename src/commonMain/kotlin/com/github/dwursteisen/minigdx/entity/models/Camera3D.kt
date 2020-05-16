package com.github.dwursteisen.minigdx.entity.models

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.inverse
import com.curiouscreature.kotlin.math.transpose
import com.github.dwursteisen.minigdx.Coordinate
import com.github.dwursteisen.minigdx.Degree
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.entity.CanMove
import com.github.dwursteisen.minigdx.entity.Entity
import com.github.dwursteisen.minigdx.entity.delegate.Movable
import com.github.dwursteisen.minigdx.gl
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.input.TouchSignal
import com.github.dwursteisen.minigdx.inputs
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.dwursteisen.minigdx.shaders.ShaderProgram

class InvMovable : Movable() {
    override fun rotateX(angle: Degree): CanMove {
        return super.rotateX(angle.toFloat() * -1f)
    }

    override fun rotateY(angle: Degree): CanMove {
        return super.rotateY(angle.toFloat() * -1f)
    }

    override fun rotateZ(angle: Degree): CanMove {
        return super.rotateZ(angle.toFloat() * -1f)
    }

    override fun translate(x: Coordinate, y: Coordinate, z: Coordinate): CanMove {
        return super.translate(x.toFloat() * -1f, y.toFloat() * -1f, z.toFloat() * -1f)
    }

    override fun scale(x: Coordinate, y: Coordinate, z: Coordinate): CanMove {
        return super.scale(x.toFloat() * -1f, y.toFloat() * -1f, z.toFloat() * -1f)
    }
}

class Camera3D(
    var projectionMatrix: Mat4
) : Entity, CanMove by InvMovable() {

    fun draw(program: ShaderProgram) {
        val normalMatrix = transpose(inverse(modelMatrix))

        gl.uniformMatrix4fv(program.getUniform("uProjectionMatrix"), false, projectionMatrix)
        gl.uniformMatrix4fv(program.getUniform("uViewMatrix"), false, inverse(modelMatrix))
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

    fun control(delta: Seconds) {
        if (inputs.isKeyPressed(Key.ARROW_LEFT)) {
            translate(x = 5f * delta)
        } else if (inputs.isKeyPressed(Key.ARROW_RIGHT)) {
            translate(x = -5f * delta)
        }

        if (inputs.isKeyPressed(Key.ARROW_UP)) {
            translate(y = -5f * delta)
        } else if (inputs.isKeyPressed(Key.ARROW_DOWN)) {
            translate(y = 5f * delta)
        }

        if (inputs.isKeyPressed(Key.Z) ||
            inputs.isTouched(TouchSignal.TOUCH1) != null
        ) {
            translate(z = 5f * delta)
        } else if (
            inputs.isKeyPressed(Key.S) ||
            inputs.isTouched(TouchSignal.TOUCH2) != null ||
            (inputs.isTouched(TouchSignal.TOUCH1) != null && inputs.isKeyPressed(Key.CTRL))
        ) {
            translate(z = -5f * delta)
        }

        if (inputs.isKeyPressed(Key.Q)) {
            rotateY(-45f * delta)
        } else if (inputs.isKeyPressed(Key.D)) {
            rotateY(45f * delta)
        }

        if (inputs.isKeyPressed(Key.A)) {
            rotateX(-45f * delta)
        } else if (inputs.isKeyPressed(Key.E)) {
            rotateX(45f * delta)
        }

        if (inputs.isKeyPressed(Key.W)) {
            rotateZ(-45f * delta)
        } else if (inputs.isKeyPressed(Key.C)) {
            rotateZ(45f * delta)
        }

        if (inputs.isKeyJustPressed(Key.L)) {
            println("P ->" + position)
            println("R -> " + rotation)
        }
    }

    companion object {

        fun perspective(fov: Number, aspect: Number, near: Number, far: Number): Camera3D {
            val projectionMatrix = com.curiouscreature.kotlin.math.perspective(
                fov = fov.toFloat(),
                aspect = aspect.toFloat(),
                near = near.toFloat(),
                far = far.toFloat()
            )
            return Camera3D(
                projectionMatrix = projectionMatrix
            )
        }

        fun orthographic(): Camera3D {
            val projectionMatrix = com.curiouscreature.kotlin.math.ortho(
                l = 0f, r = 10f,
                b = 0f, t = 10f,
                n = 0f, f = 10f
            )
            return Camera3D(
                projectionMatrix = projectionMatrix
            )
        }
    }
}
