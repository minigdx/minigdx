package threed.entity

import com.curiouscreature.kotlin.math.Mat4
import threed.entity.delegate.Drawable
import threed.entity.delegate.Movable
import threed.graphics.Render
import threed.math.Vector3

class BoneMesh(
    val mesh: Mesh
) : CanDraw by Drawable(Render(mesh)),
    CanMove by Movable() {

    companion object {
        fun of(mat: Mat4): BoneMesh {
            return BoneMesh(
                Mesh(
                    drawType = DrawType.LINE,
                    modelMatrix = mat,
                    vertices = arrayOf(
                        Vertice(
                            position = Vector3(0, 0, 0),
                            normal = Vector3(1, 1, 1),
                            color = Colors.RED
                        ),
                        Vertice(
                            position = Vector3(-0.25, 0, 0.25),
                            normal = Vector3(1, 1, 1),
                            color = Colors.WHITE
                        ),
                        Vertice(
                            position = Vector3(0.25, 0, 0.25),
                            normal = Vector3(1, 1, 1),
                            color = Colors.WHITE
                        ),
                        Vertice(
                            position = Vector3(0, 0, 1),
                            normal = Vector3(1, 1, 1),
                            color = Colors.BLUE
                        )
                    ),
                    verticesOrder = shortArrayOf(
                        0, 1, 2,
                        1, 2, 3
                    )
                )
            )
        }
    }
}
