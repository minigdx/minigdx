package threed.entity

import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.perspective
import threed.entity.delegate.Movable
import threed.math.Vector3

data class Camera(
    val position: Vector3,
    val target: Vector3,
    var projectionMatrix: Mat4
) : Entity, CanMove by Movable(
    position = position
) {

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
