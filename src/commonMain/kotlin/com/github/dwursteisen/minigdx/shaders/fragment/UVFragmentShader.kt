package com.github.dwursteisen.minigdx.shaders.fragment

import com.github.dwursteisen.minigdx.shaders.ShaderParameter

//language=GLSL
private val simpleFragmentShader = """
        #ifdef GL_ES
        precision highp float;
        #endif
        
        varying vec2 vUVPosition;

        uniform sampler2D uUV;

        void main() {
              gl_FragColor = texture2D(uUV, vUVPosition);
        }
    """.trimIndent()

class UVFragmentShader : FragmentShader(simpleFragmentShader) {

    val uUV =
        ShaderParameter.UniformSample2D("uUV")

    override val parameters: List<ShaderParameter> = listOf(uUV)
}
