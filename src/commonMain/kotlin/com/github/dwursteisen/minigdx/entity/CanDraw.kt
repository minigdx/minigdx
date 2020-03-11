package com.github.dwursteisen.minigdx.entity

import com.github.dwursteisen.minigdx.shaders.ShaderProgram

interface CanDraw {

    fun draw(shader: ShaderProgram)
}
