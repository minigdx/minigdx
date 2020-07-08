package com.github.dwursteisen.minigdx.shaders.vertex

import com.github.dwursteisen.minigdx.shaders.ShaderParameter

abstract class VertexShader(
    private val shader: String
) {
    open val parameters: List<ShaderParameter> = emptyList()

    override fun toString(): String = shader
}
