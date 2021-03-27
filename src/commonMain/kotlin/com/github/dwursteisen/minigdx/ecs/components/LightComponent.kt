package com.github.dwursteisen.minigdx.ecs.components

import com.github.dwursteisen.minigdx.Percent
import com.github.dwursteisen.minigdx.math.Vector3

data class Color(
    var red: Percent = 0.0,
    var green: Percent = 0.0,
    var blue: Percent = 0.0,
    var alpha: Percent = 1.0
)

class LightComponent(
    val color: Color = Color()
) : Component {

    companion object {

        val ORIGIN = Vector3()
        val TRANSPARENT_COLOR = Color(alpha = 0f)
    }
}
