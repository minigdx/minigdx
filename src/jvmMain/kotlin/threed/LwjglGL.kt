package threed

import org.lwjgl.opengl.GL30.glAttachShader
import org.lwjgl.opengl.GL30.glBindBuffer
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
import org.lwjgl.opengl.GL30.glGetAttribLocation
import org.lwjgl.opengl.GL30.glGetProgramInfoLog
import org.lwjgl.opengl.GL30.glGetProgrami
import org.lwjgl.opengl.GL30.glGetShaderInfoLog
import org.lwjgl.opengl.GL30.glGetShaderi
import org.lwjgl.opengl.GL30.glLinkProgram
import org.lwjgl.opengl.GL30.glShaderSource
import org.lwjgl.opengl.GL30.glUniformMatrix4fv
import org.lwjgl.opengl.GL30.glUseProgram
import org.lwjgl.opengl.GL30.glVertexAttribPointer
import org.lwjgl.opengl.GL30C.glGetUniformLocation
import threed.buffer.Buffer
import threed.buffer.DataSource
import threed.shaders.PlatformShaderProgram
import threed.shaders.Shader
import threed.shaders.ShaderProgram
import threed.shaders.Uniform

class LwjglGL(override val canvas: Canvas) : GL {

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
        return glGetProgrami(shaderProgram.program.address, mask)
    }

    override fun getShaderParameter(shader: Shader, mask: ByteMask): Any {
        return glGetShaderi(shader.address, mask)
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
        return Buffer(glGenBuffers())
    }

    override fun bindBuffer(target: ByteMask, buffer: Buffer) {
        glBindBuffer(target, buffer.address)
    }

    override fun bufferData(target: ByteMask, size: Int, usage: Int) {
        glBufferData(target, size.toLong(), usage)
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

    override fun drawArrays(mask: ByteMask, offset: Int, vertexCount: Int) {
        glDrawArrays(mask, offset, vertexCount)
    }

    override fun drawElements(mask: ByteMask, vertexCount: Int, type: Int, offset: Int) {
        glDrawElements(mask, vertexCount, type, offset.toLong())
    }
}
