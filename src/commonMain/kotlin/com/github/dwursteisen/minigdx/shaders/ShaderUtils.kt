package com.github.dwursteisen.minigdx.shaders

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.gl

object ShaderUtils {

    fun createShaderProgram(vertexShader: String, fragmentShader: String): ShaderProgram {
        val vertex = compileShader(vertexShader, GL.VERTEX_SHADER)
        val fragment = compileShader(fragmentShader, GL.FRAGMENT_SHADER)

        val shaderProgram = gl.createProgram()
        gl.attachShader(shaderProgram, vertex)
        gl.attachShader(shaderProgram, fragment)
        gl.linkProgram(shaderProgram)

        if (!gl.getProgramParameterB(shaderProgram, GL.LINK_STATUS)) {
            val log = gl.getProgramInfoLog(shaderProgram)
            throw RuntimeException("Shader compilation error: $log")
        }
        return shaderProgram
    }

    fun compileShader(vertexShader: String, type: Int): Shader {
        val shader = gl.createShader(type)
        gl.shaderSource(shader, vertexShader)
        gl.compileShader(shader)

        if (!gl.getShaderParameterB(shader, GL.COMPILE_STATUS)) {
            val log = gl.getShaderInfoLog(shader)
            gl.deleteShader(shader)
            throw RuntimeException("Shader compilation error: $log")
        }
        return shader
    }
}
