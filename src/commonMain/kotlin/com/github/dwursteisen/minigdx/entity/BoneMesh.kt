package com.github.dwursteisen.minigdx.entity

import com.curiouscreature.kotlin.math.Float4
import com.curiouscreature.kotlin.math.Mat4
import com.github.dwursteisen.minigdx.entity.delegate.Drawable
import com.github.dwursteisen.minigdx.entity.delegate.Movable
import com.github.dwursteisen.minigdx.graphics.Render
import com.github.dwursteisen.minigdx.math.Vector3

class BoneMesh(
    val mesh: Mesh
) : CanDraw by Drawable(Render(mesh)),
    CanMove by Movable() {

    // FIXME: J'ai besoin de deux matrix.
    //  point de départ et point d'arrivé.
    //  je peux lui coller deux matrix global.
    //  je peux plutôt utiliser la matrice locale du fils pour calculer le vertex
    //   modelMatrix = globalMatrix
    // of(base: Mat4, edge: Mat4 // localMatrix) {
    //     prendre chaque vertice * edge
    // }
    companion object {
        fun Float4.toVector3(): Vector3 {
            return Vector3(x, y, z)
        }

        fun of(mat: Mat4, local: Mat4): BoneMesh {
            return BoneMesh(
                Mesh(
                    drawType = DrawType.TRIANGLE,
                    modelMatrix = mat,
                    vertices = arrayOf(
                        Vertice(
                            position = (local * Float4(0f, 0f, 0f, 1f)).toVector3(),
                            normal = Vector3(1, 1, 1),
                            color = Colors.RED
                        ),
                        Vertice(
                            position = (local * Float4(-0.25f, 0.25f, 0f, 1f)).toVector3(),
                            normal = Vector3(1, 1, 1),
                            color = Colors.WHITE
                        ),
                        Vertice(
                            position = (local * Float4(0.25f, 0.25f, 0f, 1f)).toVector3(),
                            normal = Vector3(1, 1, 1),
                            color = Colors.WHITE
                        ),
                        Vertice(
                            position = (local * Float4(0f, 1f, 0f, 1f)).toVector3(),
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
