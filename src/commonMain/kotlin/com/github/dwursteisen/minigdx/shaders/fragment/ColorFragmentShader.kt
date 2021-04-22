package com.github.dwursteisen.minigdx.shaders.fragment

//language=GLSL
private val simpleFragmentShader =
    """
        #ifdef GL_ES
        precision highp float;
        #endif
        
        varying vec4 vColor;

        void main() {
              gl_FragColor = vColor;
        }
    """.trimIndent()

class ColorFragmentShader : FragmentShader(simpleFragmentShader)
