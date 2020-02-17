package threed

import org.khronos.webgl.Float32Array
import org.khronos.webgl.Int32Array
import org.khronos.webgl.WebGLRenderingContext
import threed.buffer.Buffer
import threed.buffer.DataSource
import threed.shaders.PlatformShaderProgram
import threed.shaders.Shader
import threed.shaders.ShaderProgram
import threed.shaders.Uniform

class WebGL(private val gl: WebGLRenderingContext, override val canvas: Canvas) : GL {

    override fun clearColor(r: Percent, g: Percent, b: Percent, a: Percent) {
        gl.clearColor(r.toPercent(), g.toPercent(), b.toPercent(), a.toPercent())
    }

    override fun clear(mask: ByteMask) {
        gl.clear(mask)
    }

    override fun clearDepth(depth: Number) {
        gl.clearDepth(depth.toFloat())
    }

    override fun enable(mask: ByteMask) {
        gl.enable(mask)
    }

    override fun depthFunc(target: ByteMask) {
        gl.depthFunc(target)
    }

    override fun vertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, offset: Int) {
        gl.vertexAttribPointer(index, size, type, normalized, stride, offset)
    }

    override fun createProgram(): ShaderProgram {
        return ShaderProgram(PlatformShaderProgram(gl.createProgram()!!))
    }

    override fun useProgram(shaderProgram: ShaderProgram) {
        gl.useProgram(shaderProgram.program.delegate)
    }

    override fun getAttribLocation(shaderProgram: ShaderProgram, name: String): Int {
        return gl.getAttribLocation(shaderProgram.program.delegate, name)
    }

    override fun enableVertexAttribArray(index: Int) {
        gl.enableVertexAttribArray(index)
    }

    override fun getUniformLocation(shaderProgram: ShaderProgram, name: String): Uniform {
        return Uniform(gl.getUniformLocation(shaderProgram.program.delegate, name)!!)
    }

    override fun uniformMatrix4fv(uniform: Uniform, transpose: Boolean, data: Array<Float>) {
        gl.uniformMatrix4fv(uniform.uniformLocation, transpose, data)
    }

    override fun attachShader(shaderProgram: ShaderProgram, shader: Shader) {
        gl.attachShader(shaderProgram.program.delegate, shader.delegate)
    }

    override fun linkProgram(shaderProgram: ShaderProgram) {
        gl.linkProgram(shaderProgram.program.delegate)
    }

    override fun getProgramParameter(shaderProgram: ShaderProgram, mask: ByteMask): Any {
        return gl.getProgramParameter(shaderProgram.program.delegate, mask)!!
    }

    override fun getShaderParameter(shader: Shader, mask: ByteMask): Any {
        return gl.getShaderParameter(shader.delegate, mask)!!
    }

    override fun createShader(type: ByteMask): Shader {
        return Shader(gl.createShader(type)!!)
    }

    override fun shaderSource(shader: Shader, source: String) {
        gl.shaderSource(shader.delegate, source)
    }

    override fun compileShader(shader: Shader) {
        gl.compileShader(shader.delegate)
    }

    override fun getShaderInfoLog(shader: Shader): String {
        return gl.getShaderInfoLog(shader.delegate) ?: ""
    }

    override fun deleteShader(shader: Shader) {
        gl.deleteShader(shader.delegate)
    }

    override fun getProgramInfoLog(shader: ShaderProgram): String {
        return gl.getProgramInfoLog(shader.program.delegate) ?: ""
    }

    override fun createBuffer(): Buffer {
        return Buffer(gl.createBuffer()!!)
    }

    override fun bindBuffer(target: ByteMask, buffer: Buffer) {
        gl.bindBuffer(target, buffer.delegate)
    }

    override fun bufferData(target: ByteMask, size: Int, usage: Int) {
        gl.bufferData(target, size, usage)
    }

    override fun bufferData(target: ByteMask, data: DataSource, usage: Int) {
        val converted = when (data) {
            is DataSource.FloatDataSource -> Float32Array(data.floats.toTypedArray())
            is DataSource.IntDataSource -> Int32Array(data.ints.toTypedArray())
            is DataSource.DoubleDataSource -> TODO("Not supported")
        }
        gl.bufferData(target, converted, usage)
    }

    override fun drawArrays(mask: ByteMask, offset: Int, vertexCount: Int) {
        gl.drawArrays(mask, offset, vertexCount)
    }
}
