package threed.entity

import threed.math.Matrix4
import threed.math.Vector3
import kotlin.math.PI

data class Camera(
    val position: Vector3,
    val target: Vector3,
    val projectionMatrix: Matrix4,
    val modelMatrix: Matrix4 = Matrix4.identity()
) : Entity {

    override fun translate(x: Number, y: Number, z: Number): Entity {
        position.add(x.toFloat(), y.toFloat(), z.toFloat())
        target.add(x.toFloat(), y.toFloat(), z.toFloat())

        modelMatrix.translate(Vector3(x, y, z))
        return this
    }

    companion object {

        fun create(fov: Number, aspect: Number, near: Number, far: Number): Camera {
            // en radians
            val fieldOfView = fov.toFloat() * PI.toFloat() / 180f
            val projectionMatrix = Matrix4.projection(
                fov = fieldOfView,
                aspect = aspect,
                near = near,
                far = far
            )
            return Camera(
                position = Vector3(),
                target = Vector3(),
                projectionMatrix = projectionMatrix
            )
        }
    }
}
