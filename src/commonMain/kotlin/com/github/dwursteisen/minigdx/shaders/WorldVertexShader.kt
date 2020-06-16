package com.github.dwursteisen.minigdx.shaders

class WorldVertexShader : VertexShader(
    shader = DefaultShaders.simpleVertexShader
) {
    val uModelView =
        ShaderParameter.UniformMat4("uModelView")
    val aVertexPosition =
        ShaderParameter.AttributeVec3("aVertexPosition")
    val aUVPosition = ShaderParameter.AttributeVec2("aUVPosition")

    override val parameters: List<ShaderParameter> = listOf(
        uModelView,
        aVertexPosition,
        aUVPosition
    )
}
