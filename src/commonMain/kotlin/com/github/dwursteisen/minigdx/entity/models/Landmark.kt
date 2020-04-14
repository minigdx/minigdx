package com.github.dwursteisen.minigdx.entity.models

import com.github.dwursteisen.minigdx.entity.CanDraw
import com.github.dwursteisen.minigdx.entity.CanMove
import com.github.dwursteisen.minigdx.entity.delegate.Model
import com.github.dwursteisen.minigdx.entity.delegate.Movable
import com.github.dwursteisen.minigdx.entity.primitives.Colors
import com.github.dwursteisen.minigdx.entity.primitives.DrawType
import com.github.dwursteisen.minigdx.entity.primitives.Mesh
import com.github.dwursteisen.minigdx.entity.primitives.Vertice
import com.github.dwursteisen.minigdx.math.Vector3

class Landmark(val mesh: Mesh) : CanMove by Movable(), CanDraw by Model(mesh) {

    companion object {
        fun of(): Landmark {
            return Landmark(
                Mesh(
                    name = "landmark",
                    drawType = DrawType.LINE,
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
                        0, 1,
                        0, 2,
                        0, 3
                    )
                )
            )
        }
    }
}
