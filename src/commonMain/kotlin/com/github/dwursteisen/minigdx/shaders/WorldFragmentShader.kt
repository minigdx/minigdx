package com.github.dwursteisen.minigdx.shaders

class WorldFragmentShader : FragmentShader(DefaultShaders.simpleFragmentShader) {

    val uUV =
        ShaderParameter.UniformSample2D("uUV")

    override val parameters: List<ShaderParameter> = listOf(uUV)
}
