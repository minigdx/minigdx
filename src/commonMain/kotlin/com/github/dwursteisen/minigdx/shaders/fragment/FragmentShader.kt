package com.github.dwursteisen.minigdx.shaders.fragment

import com.github.dwursteisen.minigdx.shaders.ShaderParameter

abstract class FragmentShader(
    private val shader: String
) {
    open val parameters: List<ShaderParameter> = emptyList()

    override fun toString(): String = shader
}
