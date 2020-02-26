package threed.entity

import com.curiouscreature.kotlin.math.Mat4
import threed.graphics.Render
import threed.math.Vector3
import threed.shaders.ShaderProgram


interface CanDraw {
    fun draw(shader: ShaderProgram)
}

class Drawable(val render: Render) : CanDraw {

    override fun draw(shader: ShaderProgram) {
        render.draw(shader)
    }
}

class BoneMesh(
    val mesh: Mesh
) : CanDraw by Drawable(Render(mesh)),
    CanMove by Movable() {

    companion object {
        fun of(mat: Mat4): BoneMesh {
            return BoneMesh(
                Mesh(
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
            ).also { it.modelMatrix = mat }
        }
    }
}