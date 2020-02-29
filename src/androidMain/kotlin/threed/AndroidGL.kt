package threed

import android.opengl.GLES20.glAttachShader
import android.opengl.GLES20.glBindBuffer
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
import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetProgramInfoLog
import android.opengl.GLES20.glGetShaderInfoLog
import android.opengl.GLES20.glGetUniformLocation
import android.opengl.GLES20.glLinkProgram
import android.opengl.GLES20.glShaderSource
import android.opengl.GLES20.glUniformMatrix4fv
import android.opengl.GLES20.glUseProgram
import android.opengl.GLES20.glVertexAttribPointer
import java.nio.DoubleBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer
import threed.buffer.Buffer
import threed.buffer.DataSource
import threed.shaders.PlatformShaderProgram
import threed.shaders.Shader
import threed.shaders.ShaderProgram
import threed.shaders.Uniform

fun FloatArray.asBuffer() = FloatBuffer.wrap(this)
fun IntArray.asBuffer() = IntBuffer.wrap(this)
fun DoubleArray.asBuffer() = DoubleBuffer.wrap(this)
fun ShortArray.asBuffer() = ShortBuffer.wrap(this)

class AndroidGL(override val canvas: Canvas) : GL {

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

    override fun createProgram(): ShaderProgram {
        return ShaderProgram(PlatformShaderProgram(glCreateProgram()))
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
        TODO("Not implemented yet")
        // return glGetProgrami(shaderProgram.program.address, mask)
    }

    override fun getShaderParameter(shader: Shader, mask: ByteMask): Any {
        TODO("Not implemented yet")
        // return glGetShaderi(shader.address, mask)
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

    override fun bufferData(target: ByteMask, size: Int, usage: Int) {
        TODO("Not supported")
    }

    override fun bufferData(target: ByteMask, data: DataSource, usage: Int) {
        when (data) {
            is DataSource.FloatDataSource -> glBufferData(target, data.floats.size, data.floats.asBuffer(), usage)
            is DataSource.IntDataSource -> glBufferData(target, data.ints.size, data.ints.asBuffer(), usage)
            is DataSource.ShortDataSource -> glBufferData(target, data.shorts.size, data.shorts.asBuffer(), usage)
            is DataSource.UIntDataSource -> glBufferData(target, data.ints.size, data.ints.asBuffer(), usage)
            is DataSource.DoubleDataSource -> glBufferData(target, data.double.size, data.double.asBuffer(), usage)
        }
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

    override fun uniformMatrix4fv(uniform: Uniform, transpose: Boolean, data: Array<Float>) {
        glUniformMatrix4fv(uniform.address, data.size, transpose, data.toFloatArray(), 0)
    }

    override fun drawArrays(mask: ByteMask, offset: Int, vertexCount: Int) {
        glDrawArrays(mask, offset, vertexCount)
    }

    override fun drawElements(mask: ByteMask, vertexCount: Int, type: Int, offset: Int) {
        glDrawElements(mask, vertexCount, type, offset)
    }
}
