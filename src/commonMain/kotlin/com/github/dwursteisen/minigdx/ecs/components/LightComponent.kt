package com.github.dwursteisen.minigdx.ecs.components

import com.github.dwursteisen.minigdx.Percent
import com.github.dwursteisen.minigdx.math.Vector3

data class Color(
    var red: Percent = 0.0f,
    var green: Percent = 0.0f,
    var blue: Percent = 0.0f,
    var alpha: Percent = 1.0f
)

class LightComponent(
    val color: Color = Color(),
    var intensity: Int
) : Component {

    companion object {

        val ORIGIN = Vector3()
        val TRANSPARENT_COLOR = Color(alpha = 0f)
    }
}
