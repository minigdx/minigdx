package com.github.dwursteisen.minigdx.entity.models

import com.github.dwursteisen.minigdx.entity.CanDraw
import com.github.dwursteisen.minigdx.entity.CanMove
import com.github.dwursteisen.minigdx.entity.delegate.Drawable
import com.github.dwursteisen.minigdx.entity.primitives.Color
import com.github.dwursteisen.minigdx.entity.primitives.Colors
import com.github.dwursteisen.minigdx.entity.primitives.Mesh
import com.github.dwursteisen.minigdx.entity.primitives.Vertice
import com.github.dwursteisen.minigdx.math.Vector3

class Cube(
    mesh: Mesh,
    private val delegate: Drawable = Drawable(mesh)
) : CanDraw by delegate, CanMove by delegate {

    constructor(name: String, color: Color = Colors.WHITE) : this(
        Mesh(
            name = name,
            vertices = arrayOf(
                Vertice(
                    position = Vector3(
                        x = -1,
                        y = 1,
                        z = 1
                    ), color = color
                ), // 0
                Vertice(
                    position = Vector3(1, 1, 1),
                    color = color
                ), // 1
                Vertice(
                    position = Vector3(-1, -1, 1),
                    color = color
                ), // 2
                Vertice(
                    position = Vector3(1, -1, 1),
                    color = color
                ), // 3
                Vertice(
                    position = Vector3(-1, -1, -1),
                    color = color
                ), // 4
                Vertice(
                    position = Vector3(-1, 1, -1),
                    color = color
                ), // 5
                Vertice(
                    position = Vector3(1, 1, -1),
                    color = color
                ), // 6
                Vertice(
                    position = Vector3(1, -1, -1),
                    color = color
                ) // 7
            ),
            verticesOrder = shortArrayOf(
                // front
                0, 1, 2,
                2, 1, 3,
                // left
                0, 2, 5,
                5, 2, 4,
                // right
                1, 3, 7,
                7, 1, 6,
                // back
                7, 6, 5,
                5, 7, 4,
                // up
                0, 1, 5,
                1, 5, 6,
                // down
                2, 3, 4,
                3, 4, 7
            )
        )
    )
}
