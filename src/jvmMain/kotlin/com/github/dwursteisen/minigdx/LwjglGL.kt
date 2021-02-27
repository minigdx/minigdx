package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.file.TextureImage
import com.github.dwursteisen.minigdx.shaders.Buffer
import com.github.dwursteisen.minigdx.shaders.DataSource
import com.github.dwursteisen.minigdx.shaders.PlatformShaderProgram
import com.github.dwursteisen.minigdx.shaders.Shader
import com.github.dwursteisen.minigdx.shaders.ShaderProgram
import com.github.dwursteisen.minigdx.shaders.TextureReference
import com.github.dwursteisen.minigdx.shaders.Uniform
import java.nio.ByteBuffer
import org.lwjgl.opengl.GL11.glDisable
import org.lwjgl.opengl.GL11.glTexParameteri
import org.lwjgl.opengl.GL13.glActiveTexture
import org.lwjgl.opengl.GL20.glUniform1f
import org.lwjgl.opengl.GL20.glUniform2i
import org.lwjgl.opengl.GL20.glUniform3f
import org.lwjgl.opengl.GL20.glUniform3i
import org.lwjgl.opengl.GL20.glUniform4f
import org.lwjgl.opengl.GL30.glAttachShader
import org.lwjgl.opengl.GL30.glBindBuffer
import org.lwjgl.opengl.GL30.glBindTexture
import org.lwjgl.opengl.GL30.glBlendFunc
import org.lwjgl.opengl.GL30.glBufferData
import org.lwjgl.opengl.GL30.glClear
import org.lwjgl.opengl.GL30.glClearColor
import org.lwjgl.opengl.GL30.glClearDepth
import org.lwjgl.opengl.GL30.glCompileShader
import org.lwjgl.opengl.GL30.glCreateProgram
import org.lwjgl.opengl.GL30.glCreateShader
import org.lwjgl.opengl.GL30.glDeleteShader
import org.lwjgl.opengl.GL30.glDepthFunc
import org.lwjgl.opengl.GL30.glDrawArrays
import org.lwjgl.opengl.GL30.glDrawElements
import org.lwjgl.opengl.GL30.glEnable
import org.lwjgl.opengl.GL30.glEnableVertexAttribArray
import org.lwjgl.opengl.GL30.glGenBuffers
import org.lwjgl.opengl.GL30.glGenTextures
import org.lwjgl.opengl.GL30.glGenerateMipmap
import org.lwjgl.opengl.GL30.glGetAttribLocation
import org.lwjgl.opengl.GL30.glGetProgramInfoLog
import org.lwjgl.opengl.GL30.glGetProgrami
import org.lwjgl.opengl.GL30.glGetShaderInfoLog
import org.lwjgl.opengl.GL30.glGetShaderi
import org.lwjgl.opengl.GL30.glGetString
import org.lwjgl.opengl.GL30.glLinkProgram
import org.lwjgl.opengl.GL30.glShaderSource
import org.lwjgl.opengl.GL30.glTexImage2D
import org.lwjgl.opengl.GL30.glUniform1i
import org.lwjgl.opengl.GL30.glUniform2f
import org.lwjgl.opengl.GL30.glUniformMatrix4fv
import org.lwjgl.opengl.GL30.glUseProgram
import org.lwjgl.opengl.GL30.glVertexAttribPointer
import org.lwjgl.opengl.GL30.glViewport
import org.lwjgl.opengl.GL30C.glGetUniformLocation

class LwjglGL : GL {

    override fun clearColor(r: Percent, g: Percent, b: Percent, a: Percent) {
        glClearColor(r.toPercent(), g.toPercent(), b.toPercent(), a.toPercent())
    }

    override fun clear(mask: ByteMask) {
        glClear(mask)
    }

    override fun clearDepth(depth: Number) {
        glClearDepth(depth.toDouble())
    }

    override fun enable(mask: ByteMask) {
        glEnable(mask)
    }

    override fun disable(mask: ByteMask) {
        glDisable(mask)
    }

    override fun blendFunc(sfactor: ByteMask, dfactor: ByteMask) {
        glBlendFunc(sfactor, dfactor)
    }

    override fun createProgram(): ShaderProgram {
        return ShaderProgram(this, PlatformShaderProgram(glCreateProgram()))
    }

    override fun getAttribLocation(shaderProgram: ShaderProgram, name: String): Int {
        return glGetAttribLocation(shaderProgram.program.address, name)
    }

    override fun getUniformLocation(shaderProgram: ShaderProgram, name: String): Uniform {
        return Uniform(glGetUniformLocation(shaderProgram.program.address, name))
    }

    override fun attachShader(shaderProgram: ShaderProgram, shader: Shader) {
        glAttachShader(shaderProgram.program.address, shader.address)
    }

    override fun linkProgram(shaderProgram: ShaderProgram) {
        glLinkProgram(shaderProgram.program.address)
    }

    override fun getProgramParameter(shaderProgram: ShaderProgram, mask: ByteMask): Any {
        return glGetProgrami(shaderProgram.program.address, mask)
    }

    override fun getShaderParameter(shader: Shader, mask: ByteMask): Any {
        return glGetShaderi(shader.address, mask)
    }

    override fun getProgramParameterB(shaderProgram: ShaderProgram, mask: ByteMask): Boolean {
        return (getProgramParameter(shaderProgram, mask) as? Int) == 1
    }

    override fun getString(parameterName: Int): String? {
        return glGetString(parameterName)
    }

    override fun getShaderParameterB(shader: Shader, mask: ByteMask): Boolean {
        return (getShaderParameter(shader, mask) as? Int) == 1
    }

    override fun createShader(type: ByteMask): Shader {
        return Shader(glCreateShader(type))
    }

    override fun shaderSource(shader: Shader, source: String) {
        glShaderSource(shader.address, source)
    }

    override fun compileShader(shader: Shader) {
        glCompileShader(shader.address)
    }

    override fun getShaderInfoLog(shader: Shader): String {
        return glGetShaderInfoLog(shader.address)
    }

    override fun deleteShader(shader: Shader) {
        glDeleteShader(shader.address)
    }

    override fun getProgramInfoLog(shader: ShaderProgram): String {
        return glGetProgramInfoLog(shader.program.address)
    }

    override fun createBuffer(): Buffer {
        return Buffer(glGenBuffers())
    }

    override fun bindBuffer(target: ByteMask, buffer: Buffer) {
        glBindBuffer(target, buffer.address)
    }

    override fun bufferData(target: ByteMask, data: DataSource, usage: Int) {
        when (data) {
            is DataSource.FloatDataSource -> glBufferData(target, data.floats, usage)
            is DataSource.IntDataSource -> glBufferData(target, data.ints, usage)
            is DataSource.ShortDataSource -> glBufferData(target, data.shorts, usage)
            is DataSource.UIntDataSource -> glBufferData(target, data.ints, usage)
            is DataSource.DoubleDataSource -> glBufferData(target, data.double, usage)
        }
    }

    override fun depthFunc(target: ByteMask) {
        glDepthFunc(target)
    }

    override fun vertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int) {
        glVertexAttribPointer(index, size, type, normalized, stride, offset.toLong())
    }

    override fun enableVertexAttribArray(index: Int) {
        glEnableVertexAttribArray(index)
    }

    override fun useProgram(shaderProgram: ShaderProgram) {
        glUseProgram(shaderProgram.program.address)
    }

    override fun uniformMatrix4fv(uniform: Uniform, transpose: Boolean, data: Array<Float>) {
        glUniformMatrix4fv(uniform.address, transpose, data.toFloatArray())
    }

    override fun uniform1i(uniform: Uniform, data: Int) {
        glUniform1i(uniform.address, data)
    }

    override fun uniform2i(uniform: Uniform, a: Int, b: Int) {
        glUniform2i(uniform.address, a, b)
    }

    override fun uniform3i(uniform: Uniform, a: Int, b: Int, c: Int) {
        glUniform3i(uniform.address, a, b, c)
    }

    override fun uniform1f(uniform: Uniform, first: Float) {
        glUniform1f(uniform.address, first)
    }

    override fun uniform2f(uniform: Uniform, first: Float, second: Float) {
        glUniform2f(uniform.address, first, second)
    }

    override fun uniform3f(uniform: Uniform, first: Float, second: Float, third: Float) {
        glUniform3f(uniform.address, first, second, third)
    }

    override fun uniform4f(uniform: Uniform, first: Float, second: Float, third: Float, fourth: Float) {
        glUniform4f(uniform.address, first, second, third, fourth)
    }

    override fun drawArrays(mask: ByteMask, offset: Int, vertexCount: Int) {
        glDrawArrays(mask, offset, vertexCount)
    }

    override fun drawElements(mask: ByteMask, vertexCount: Int, type: Int, offset: Int) {
        glDrawElements(mask, vertexCount, type, offset.toLong())
    }

    override fun viewport(x: Int, y: Int, width: Int, height: Int) {
        glViewport(x, y, width, height)
    }

    override fun createTexture(): TextureReference {
        return TextureReference(glGenTextures())
    }

    override fun bindTexture(target: Int, textureReference: TextureReference) {
        glBindTexture(target, textureReference.pointer)
    }

    override fun texImage2D(
        target: Int,
        level: Int,
        internalformat: Int,
        format: Int,
        type: Int,
        source: TextureImage
    ) {
        glTexImage2D(
            target,
            level,
            internalformat,
            source.width,
            source.height,
            0,
            source.glFormat,
            source.glType,
            source.pixels
        )
    }

    override fun texImage2D(
        target: Int,
        level: Int,
        internalformat: Int,
        format: Int,
        width: Int,
        height: Int,
        type: Int,
        source: ByteArray
    ) {
        // glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
        val buffer = ByteBuffer.allocateDirect(source.size)
        buffer.put(source)
        buffer.position(0)

        glTexImage2D(
            target,
            level,
            internalformat,
            width,
            height,
            0,
            format,
            type,
            buffer
        )
    }

    override fun activeTexture(byteMask: ByteMask) {
        glActiveTexture(byteMask)
    }

    override fun texParameteri(target: Int, paramName: Int, paramValue: Int) {
        glTexParameteri(target, paramName, paramValue)
    }

    override fun generateMipmap(target: Int) {
        glGenerateMipmap(target)
    }
}
