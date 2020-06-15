package com.github.dwursteisen.minigdx.shaders

class WorldVertexShader : VertexShader(
    shader = DefaultShaders.simpleVertexShader
) {
    val uModelView =
        ShaderParameter.UniformMat4("uModelView")
    val aVertexPosition =
        ShaderParameter.AtributeVec3("aVertexPosition")

    override val parameters: List<ShaderParameter> = listOf(
        uModelView,
        aVertexPosition
    )
}
