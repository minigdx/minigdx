package com.github.dwursteisen.minigdx.entity.delegate

import com.github.dwursteisen.minigdx.entity.CanDraw
import com.github.dwursteisen.minigdx.graphics.Render
import com.github.dwursteisen.minigdx.shaders.ShaderProgram

class Drawable(val render: Render) : CanDraw {

    override fun draw(shader: ShaderProgram) {
        render.draw(shader)
    }
}
