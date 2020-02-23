package threed.entity

import com.curiouscreature.kotlin.math.Float3
import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.rotation
import com.curiouscreature.kotlin.math.translation
import threed.math.Vector3

interface CanMove {

    var modelMatrix: Mat4

    fun rotate(angles: Vector3): CanMove

    fun rotateX(angle: Float): CanMove
    fun rotateY(angle: Float): CanMove
    fun rotateZ(angle: Float): CanMove

    fun setRotationX(angle: Float): CanMove

    fun translate(move: Vector3): CanMove = translate(move.x, move.y, move.z)
    fun translate(x: Number, y: Number, z: Number): CanMove
}

class Movable : CanMove {

    override var modelMatrix: Mat4 = Mat4.identity()

    private val rotation: Vector3 = Vector3()
    private val position: Vector3 = Vector3()

    override fun rotate(angles: Vector3): CanMove {
        rotateX(angles.x)
        rotateY(angles.y)
        rotateZ(angles.z)
        return this
    }

    override fun rotateX(angle: Float): CanMove {
        rotation.x += angle
        modelMatrix *= rotation(Float3(1f, 0f, 0f), angle)
        return this
    }

    override fun rotateY(angle: Float): CanMove {
        rotation.y += angle
        modelMatrix *= rotation(Float3(0f, 1f, 0f), angle)
        return this
    }

    override fun rotateZ(angle: Float): CanMove {
        rotation.z += angle
        modelMatrix *= rotation(Float3(0f, 0f, 1f), angle)
        return this
    }

    override fun setRotationX(angle: Float): CanMove {
        val toRotate = angle - rotation.x
        return rotateX(toRotate)
    }

    override fun translate(x: Number, y: Number, z: Number): CanMove {
        position.add(x.toFloat(), y.toFloat(), z.toFloat())
        modelMatrix *= translation(Float3(x.toFloat(), y.toFloat(), z.toFloat()))
        return this
    }
}

interface Entity
