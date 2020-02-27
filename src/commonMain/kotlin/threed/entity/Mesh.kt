package threed.entity

import com.curiouscreature.kotlin.math.Mat4
import threed.entity.delegate.Movable
import threed.math.Vector3

enum class DrawType {
    TRIANGLE, LINE
}
class Mesh(
    val name: String = "unknown",
    position: Vector3 = Vector3(),
    rotation: Vector3 = Vector3(),
    modelMatrix: Mat4 = Mat4.identity(),
    val vertices: Array<Vertice> = emptyArray(),
    val verticesOrder: ShortArray,
    val drawType: DrawType = DrawType.TRIANGLE
) : CanMove by Movable(
    position = position,
    rotation = rotation,
    modelMatrix = modelMatrix
)
