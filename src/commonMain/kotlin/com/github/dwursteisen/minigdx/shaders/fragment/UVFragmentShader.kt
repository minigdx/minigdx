package com.github.dwursteisen.minigdx.shaders.fragment

import com.github.dwursteisen.minigdx.shaders.ShaderParameter

//language=GLSL
private val simpleFragmentShader =
    """
        #ifdef GL_ES
        precision highp float;
        #endif
        
        varying vec2 vUVPosition;
        varying vec4 vLighting;

        uniform sampler2D uUV;

        void main() {
              vec4 texel = texture2D(uUV, vUVPosition);
              gl_FragColor = vec4(texel.rgb * vLighting.rgb, texel.a);
        }
    """.trimIndent()

class UVFragmentShader : FragmentShader(simpleFragmentShader) {

    val uUV =
        ShaderParameter.UniformSample2D("uUV")

    override val parameters: List<ShaderParameter> = listOf(uUV)
}
