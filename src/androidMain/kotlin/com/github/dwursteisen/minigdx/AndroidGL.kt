package com.github.dwursteisen.minigdx

import android.opengl.GLES20.glActiveTexture
import android.opengl.GLES20.glAttachShader
import android.opengl.GLES20.glBindBuffer
import android.opengl.GLES20.glBindTexture
import android.opengl.GLES20.glBlendFunc
import android.opengl.GLES20.glBufferData
import android.opengl.GLES20.glClear
import android.opengl.GLES20.glClearColor
import android.opengl.GLES20.glClearDepthf
import android.opengl.GLES20.glCompileShader
import android.opengl.GLES20.glCreateProgram
import android.opengl.GLES20.glCreateShader
import android.opengl.GLES20.glDeleteShader
import android.opengl.GLES20.glDepthFunc
import android.opengl.GLES20.glDrawArrays
import android.opengl.GLES20.glDrawElements
import android.opengl.GLES20.glEnable
import android.opengl.GLES20.glEnableVertexAttribArray
import android.opengl.GLES20.glGenBuffers
import android.opengl.GLES20.glGenTextures
import android.opengl.GLES20.glGenerateMipmap
import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetProgramInfoLog
import android.opengl.GLES20.glGetProgramiv
import android.opengl.GLES20.glGetShaderInfoLog
import android.opengl.GLES20.glGetShaderiv
import android.opengl.GLES20.glGetUniformLocation
import android.opengl.GLES20.glLinkProgram
import android.opengl.GLES20.glShaderSource
import android.opengl.GLES20.glTexImage2D
import android.opengl.GLES20.glTexParameteri
import android.opengl.GLES20.glUniform1i
import android.opengl.GLES20.glUniform2f
import android.opengl.GLES20.glUniform2i
import android.opengl.GLES20.glUniform3f
import android.opengl.GLES20.glUniform3i
import android.opengl.GLES20.glUniformMatrix4fv
import android.opengl.GLES20.glUseProgram
import android.opengl.GLES20.glVertexAttribPointer
import android.opengl.GLES20.glViewport
import com.github.dwursteisen.minigdx.buffer.Buffer
import com.github.dwursteisen.minigdx.buffer.DataSource
import com.github.dwursteisen.minigdx.file.TextureImage
import com.github.dwursteisen.minigdx.shaders.PlatformShaderProgram
import com.github.dwursteisen.minigdx.shaders.Shader
import com.github.dwursteisen.minigdx.shaders.ShaderProgram
import com.github.dwursteisen.minigdx.shaders.TextureReference
import com.github.dwursteisen.minigdx.shaders.Uniform
import java.nio.ByteBuffer
import java.nio.DoubleBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

fun FloatArray.asBuffer() = FloatBuffer.wrap(this)
fun IntArray.asBuffer() = IntBuffer.wrap(this)
fun DoubleArray.asBuffer() = DoubleBuffer.wrap(this)
fun ShortArray.asBuffer() = ShortBuffer.wrap(this)

class AndroidGL(override val screen: Screen) : GL {

    override fun clearColor(r: Percent, g: Percent, b: Percent, a: Percent) {
        glClearColor(r.toPercent(), g.toPercent(), b.toPercent(), a.toPercent())
    }

    override fun clear(mask: ByteMask) {
        glClear(mask)
    }

    override fun clearDepth(depth: Number) {
        glClearDepthf(depth.toFloat())
    }

    override fun enable(mask: ByteMask) {
        glEnable(mask)
    }

    override fun blendFunc(sfactor: ByteMask, dfactor: ByteMask) {
        glBlendFunc(sfactor, dfactor)
    }

    override fun createProgram(): ShaderProgram {
        return ShaderProgram(PlatformShaderProgram(glCreateProgram()))
    }

    override fun getAttribLocation(shaderProgram: ShaderProgram, name: String): Int {
        return glGetAttribLocation(shaderProgram.program.address, name)
    }

    override fun getUniformLocation(shaderProgram: ShaderProgram, name: String): Uniform {
        val address = glGetUniformLocation(shaderProgram.program.address, name)
        return Uniform(address)
    }

    override fun attachShader(shaderProgram: ShaderProgram, shader: Shader) {
        glAttachShader(shaderProgram.program.address, shader.address)
    }

    override fun linkProgram(shaderProgram: ShaderProgram) {
        glLinkProgram(shaderProgram.program.address)
    }

    override fun getProgramParameter(shaderProgram: ShaderProgram, mask: ByteMask): Any {
        val intBuffer = IntBuffer.allocate(1)
        glGetProgramiv(shaderProgram.program.address, mask, intBuffer)
        return intBuffer[0]
    }

    override fun getShaderParameter(shader: Shader, mask: ByteMask): Any {
        val intBuffer = IntBuffer.allocate(1)
        glGetShaderiv(shader.address, mask, intBuffer)
        return intBuffer[0]
    }

    override fun getProgramParameterB(shaderProgram: ShaderProgram, mask: ByteMask): Boolean {
        return (getProgramParameter(shaderProgram, mask) as? Int) == 1
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
        val intBuffer = IntBuffer.allocate(1)
        glGenBuffers(intBuffer.limit(), intBuffer)
        return Buffer(intBuffer[0])
    }

    override fun bindBuffer(target: ByteMask, buffer: Buffer) {
        glBindBuffer(target, buffer.address)
    }

    override fun bufferData(target: ByteMask, data: DataSource, usage: Int) {
        val buffer: java.nio.Buffer = when (data) {
            is DataSource.FloatDataSource -> data.floats.asBuffer()
            is DataSource.IntDataSource -> data.ints.asBuffer()
            is DataSource.ShortDataSource -> data.shorts.asBuffer()
            is DataSource.UIntDataSource -> data.ints.asBuffer()
            is DataSource.DoubleDataSource -> data.double.asBuffer()
        }
        val factor = when (data) {
            is DataSource.FloatDataSource -> java.lang.Float.BYTES
            is DataSource.IntDataSource -> java.lang.Integer.BYTES
            is DataSource.ShortDataSource -> java.lang.Short.BYTES
            is DataSource.UIntDataSource -> java.lang.Integer.BYTES
            is DataSource.DoubleDataSource -> java.lang.Double.BYTES
        }
        glBufferData(target, buffer.capacity() * factor, buffer, usage)
    }

    override fun texParameteri(target: Int, paramName: Int, paramValue: Int) {
        glTexParameteri(target, paramName, paramValue)
    }

    override fun depthFunc(target: ByteMask) {
        glDepthFunc(target)
    }

    override fun vertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int) {
        glVertexAttribPointer(index, size, type, normalized, stride, offset)
    }

    override fun enableVertexAttribArray(index: Int) {
        glEnableVertexAttribArray(index)
    }

    override fun useProgram(shaderProgram: ShaderProgram) {
        glUseProgram(shaderProgram.program.address)
    }

    override fun createTexture(): TextureReference {
        val ints = intArrayOf(0)
        glGenTextures(1, ints, 0)
        return TextureReference(pointer = ints[0])
    }

    override fun activeTexture(byteMask: ByteMask) {
        glActiveTexture(byteMask)
    }

    override fun bindTexture(target: Int, textureReference: TextureReference) {
        glBindTexture(target, textureReference.pointer)
    }

    override fun uniformMatrix4fv(uniform: Uniform, transpose: Boolean, data: Array<Float>) {
        // divided by 16 took from libgdx.
        glUniformMatrix4fv(uniform.address, data.size / 16, transpose, data.toFloatArray(), 0)
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

    override fun uniform2f(uniform: Uniform, first: Float, second: Float) {
        glUniform2f(uniform.address, first, second)
    }

    override fun uniform3f(uniform: Uniform, first: Float, second: Float, third: Float) {
        glUniform3f(uniform.address, first, second, third)
    }

    override fun drawArrays(mask: ByteMask, offset: Int, vertexCount: Int) {
        glDrawArrays(mask, offset, vertexCount)
    }

    override fun drawElements(mask: ByteMask, vertexCount: Int, type: Int, offset: Int) {
        glDrawElements(mask, vertexCount, type, offset)
    }

    override fun viewport(x: Int, y: Int, width: Int, height: Int) {
        glViewport(x, y, width, height)
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
            format,
            type,
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
        val buffer = ByteBuffer.allocateDirect(source.size)
        buffer.put(source)
        buffer.position(0)
        glTexImage2D(target, level, internalformat, width, height, 0, format, type, buffer)
    }

    override fun generateMipmap(target: Int) {
        glGenerateMipmap(target)
    }
}
