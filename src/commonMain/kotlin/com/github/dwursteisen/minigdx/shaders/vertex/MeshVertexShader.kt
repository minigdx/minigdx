package com.github.dwursteisen.minigdx.shaders.vertex

import com.github.dwursteisen.minigdx.shaders.ShaderParameter

//language=GLSL
private val simpleVertexShader =
    """
        #ifdef GL_ES
        precision highp float;
        #endif
        
        uniform mat4 uModelView;
        attribute vec3 aVertexPosition;
        attribute vec2 aUVPosition;
        
        varying vec2 vUVPosition;
        
        void main() {
            gl_Position = uModelView * vec4(aVertexPosition, 1.0);
            vUVPosition = aUVPosition;
        }
    """.trimIndent()

class MeshVertexShader : VertexShader(
    shader = simpleVertexShader
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
