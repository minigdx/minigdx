package com.github.dwursteisen.minigdx.entity

import com.github.dwursteisen.minigdx.entity.delegate.Drawable
import com.github.dwursteisen.minigdx.entity.delegate.Movable
import com.github.dwursteisen.minigdx.math.Vector3

class Landmark(val mesh: Mesh) : CanMove by Movable(), CanDraw by Drawable(mesh) {

    companion object {
        fun of(): Landmark {
            return Landmark(
                Mesh(
                    name = "landmark",
                    drawType = DrawType.LINE,
                    position = Vector3(),
                    rotation = Vector3(),
                    vertices = arrayOf(
                        Vertice(
                            position = Vector3(0, 0, 0),
                            normal = Vector3(1, 1, 1),
                            color = Colors.WHITE
                        ),
                        Vertice(
                            position = Vector3(1, 0, 0),
                            normal = Vector3(1, 1, 1),
                            color = Colors.BLUE
                        ),
                        Vertice(
                            position = Vector3(0, 1, 0),
                            normal = Vector3(1, 1, 1),
                            color = Colors.RED
                        ),
                        Vertice(
                            position = Vector3(0, 0, 1),
                            normal = Vector3(1, 1, 1),
                            color = Colors.GREEN
                        )
                    ),
                    verticesOrder = shortArrayOf(
                        0, 1, 0,
                        0, 2, 0,
                        0, 3, 0
                    )
                )
            )
        }
    }
}
