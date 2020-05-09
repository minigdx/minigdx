package com.github.dwursteisen.minigdx.entity.models

import com.github.dwursteisen.minigdx.entity.CanMoveAndDraw
import com.github.dwursteisen.minigdx.entity.primitives.Colors

class Landmark private constructor(private val structure: GroupModels = GroupModels()) : CanMoveAndDraw by structure {

    private constructor(vararg structure: CanMoveAndDraw) : this(GroupModels(structure.toMutableList()))

    companion object {
        fun of(): Landmark {
            return Landmark(
                Cube("").also { it.setScale(0.5f, 0.5f, 0.5f) }, // origin
                Cube(name = "X", color = Colors.RED)
                    .also { it.translate(x = 1f).setScale(0.5f, 0.5f, 0.5f) }, // X
                Cube(name = "Y", color = Colors.GREEN)
                    .also { it.translate(y = 1f).setScale(0.5f, 0.5f, 0.5f) }, // Y
                Cube(name = "Z", color = Colors.BLUE)
                    .also { it.translate(z = 1f).setScale(0.5f, 0.5f, 0.5f) } // Z
            )
        }
    }
}
