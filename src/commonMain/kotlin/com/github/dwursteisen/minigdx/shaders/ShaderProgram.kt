package com.github.dwursteisen.minigdx.shaders

import com.github.dwursteisen.minigdx.gl

expect class PlatformShaderProgram

class ShaderProgram(val program: PlatformShaderProgram) {

    private val attributes = mutableMapOf<String, Int>()

    private val uniforms = mutableMapOf<String, Uniform>()

    fun createAttrib(name: String) {
        attributes[name] = gl.getAttribLocation(this, name)
    }

    fun createUniform(name: String) {
        uniforms[name] = gl.getUniformLocation(this, name)
    }

    fun getAttrib(name: String): Int = attributes[name] ?: throw RuntimeException("Attributes '$name' not created!")

    fun getUniform(name: String): Uniform {
        return uniforms[name] ?: throw RuntimeException("Uniform '$name' not created!")
    }
}
