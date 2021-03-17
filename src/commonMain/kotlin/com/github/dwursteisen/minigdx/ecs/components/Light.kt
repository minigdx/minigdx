package com.github.dwursteisen.minigdx.ecs.components

import com.github.dwursteisen.minigdx.Percent

data class Color(
    var red: Percent = 0.0,
    var green: Percent = 0.0,
    var blue: Percent = 0.0,
    var alpha: Percent = 1.0
)

class Light(
    val color: Color = Color()
) : Component
