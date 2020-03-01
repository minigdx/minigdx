package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.buffer.Buffer
import com.github.dwursteisen.minigdx.buffer.DataSource
import com.github.dwursteisen.minigdx.shaders.Shader
import com.github.dwursteisen.minigdx.shaders.ShaderProgram
import com.github.dwursteisen.minigdx.shaders.Uniform

class MetalGL : GL {
    override val canvas: Canvas
        get() = TODO("not implemented")

    override fun clearColor(r: Percent, g: Percent, b: Percent, a: Percent) {
        TODO("not implemented")
    }

    override fun clear(mask: ByteMask) {
        TODO("not implemented")
    }

    override fun clearDepth(depth: Number) {
        TODO("not implemented")
    }

    override fun enable(mask: ByteMask) {
        TODO("not implemented")
    }

    override fun createProgram(): ShaderProgram {
        TODO("not implemented")
    }

    override fun getAttribLocation(shaderProgram: ShaderProgram, name: String): Int {
        TODO("not implemented")
    }

    override fun getUniformLocation(shaderProgram: ShaderProgram, name: String): Uniform {
        TODO("not implemented")
    }

    override fun attachShader(shaderProgram: ShaderProgram, shader: Shader) {
        TODO("not implemented")
    }

    override fun linkProgram(shaderProgram: ShaderProgram) {
        TODO("not implemented")
    }

    override fun getProgramParameter(shaderProgram: ShaderProgram, mask: ByteMask): Any {
        TODO("not implemented")
    }

    override fun getShaderParameter(shader: Shader, mask: ByteMask): Any {
        TODO("not implemented")
    }

    override fun createShader(type: ByteMask): Shader {
        TODO("not implemented")
    }

    override fun shaderSource(shader: Shader, source: String) {
        TODO("not implemented")
    }

    override fun compileShader(shader: Shader) {
        TODO("not implemented")
    }

    override fun getShaderInfoLog(shader: Shader): String {
        TODO("not implemented")
    }

    override fun deleteShader(shader: Shader) {
        TODO("not implemented")
    }

    override fun getProgramInfoLog(shader: ShaderProgram): String {
        TODO("not implemented")
    }

    override fun createBuffer(): Buffer {
        TODO("not implemented")
    }

    override fun bindBuffer(target: ByteMask, buffer: Buffer) {
        TODO("not implemented")
    }

    override fun bufferData(target: ByteMask, size: Int, usage: Int) {
        TODO("not implemented")
    }

    override fun bufferData(target: ByteMask, data: DataSource, usage: Int) {
        TODO("not implemented")
    }

    override fun depthFunc(target: ByteMask) {
        TODO("not implemented")
    }

    override fun vertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int) {
        TODO("not implemented")
    }

    override fun enableVertexAttribArray(index: Int) {
        TODO("not implemented")
    }

    override fun useProgram(shaderProgram: ShaderProgram) {
        TODO("not implemented")
    }

    override fun uniformMatrix4fv(uniform: Uniform, transpose: Boolean, data: Array<Float>) {
        TODO("not implemented")
    }

    override fun drawArrays(mask: ByteMask, offset: Int, vertexCount: Int) {
        TODO("not implemented")
    }

    override fun drawElements(mask: ByteMask, vertexCount: Int, type: Int, offset: Int) {
        TODO("not implemented")
    }
}
