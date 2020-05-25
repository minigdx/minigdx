package com.github.dwursteisen.minigdx.entity.models

import com.github.dwursteisen.minigdx.entity.CanDraw
import com.github.dwursteisen.minigdx.entity.CanMove
import com.github.dwursteisen.minigdx.entity.primitives.Colors

class Landmark private constructor(private val structure: GroupModels = GroupModels()) :
    CanMove by structure,
    CanDraw by structure {

    companion object {
        fun of(): Landmark {
            val landmark = Landmark()

            landmark.structure.add(Cube("").also { it.setScale(0.5f, 0.5f, 0.5f) }) // origin
            landmark.structure.add(Cube(name = "X", color = Colors.RED)
                .also { it.translate(x = 1f).setScale(0.5f, 0.5f, 0.5f) }) // X
            landmark.structure.add(Cube(name = "Y", color = Colors.GREEN)
                .also { it.translate(y = 1f).setScale(0.5f, 0.5f, 0.5f) }) // Y
            landmark.structure.add(Cube(name = "Z", color = Colors.BLUE)
                .also { it.translate(z = 1f).setScale(0.5f, 0.5f, 0.5f) }) // Z

            return landmark
        }
    }
}
