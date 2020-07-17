package com.github.dwursteisen.minigdx.graphics.compilers

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.ecs.components.gl.GLResourceComponent

interface GLResourceCompiler {

    fun compile(gl: GL, component: GLResourceComponent)
}
