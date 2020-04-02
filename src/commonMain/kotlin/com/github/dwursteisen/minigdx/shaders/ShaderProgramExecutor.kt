package com.github.dwursteisen.minigdx.shaders

import com.github.dwursteisen.minigdx.gl

class ShaderProgramExecutor(private val program: ShaderProgram) {

    fun render(block: (ShaderProgram) -> Unit) {
        gl.useProgram(program)
        block(program)
    }
}
