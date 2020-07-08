package com.github.dwursteisen.minigdx.shaders.vertex

import com.github.dwursteisen.minigdx.shaders.ShaderParameter

//language=GLSL
private val simpleVertexShader = """
        #ifdef GL_ES
        precision highp float;
        #endif
        
        uniform mat4 uModelView;
        attribute vec3 aVertexPosition;
        attribute vec4 aColor;
        
        varying vec4 vColor;
        
        void main() {
            gl_Position = uModelView * vec4(aVertexPosition, 1.0);
            vColor = aColor;
        }
    """.trimIndent()

class BoundingBoxVertexShader : VertexShader(simpleVertexShader) {

    val uModelView =
        ShaderParameter.UniformMat4("uModelView")
    val aVertexPosition =
        ShaderParameter.AttributeVec3("aVertexPosition")

    val aColor = ShaderParameter.AttributeVec4("aColor")

    override val parameters: List<ShaderParameter> = listOf(
        uModelView, aVertexPosition, aColor
    )
}
