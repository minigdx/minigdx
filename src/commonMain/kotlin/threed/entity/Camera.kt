package threed.entity

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.perspective
import com.curiouscreature.kotlin.math.rotation
import com.curiouscreature.kotlin.math.translation
import threed.math.Vector3

data class Camera(
    val position: Vector3,
    val target: Vector3,
    var projectionMatrix: Mat4
) : Entity, CanMove by Movable() {

    override fun translate(x: Number, y: Number, z: Number): Entity {
        position.add(x.toFloat(), y.toFloat(), z.toFloat())
        target.add(x.toFloat(), y.toFloat(), z.toFloat())

        modelMatrix = modelMatrix.times(translation(Float3(x.toFloat(), y.toFloat(), z.toFloat())))
        return this
    }

    fun rotate(x: Number, y: Number, z: Number): Entity {
        val axesX = rotation(Float3(1f, 0f, 0f), x.toFloat())
        val axesY = rotation(Float3(0f, 1f, 0f), y.toFloat())
        val axesZ = rotation(Float3(0f, 0f, 1f), z.toFloat())
        modelMatrix = modelMatrix * axesX * axesY * axesZ
        return this
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
                position = Vector3(),
                target = Vector3(),
                projectionMatrix = projectionMatrix
            )
        }
    }
}
