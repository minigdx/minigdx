package com.github.dwursteisen.minigdx.graphics

import com.github.dwursteisen.minigdx.ByteMask
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.Percent
import com.github.dwursteisen.minigdx.Screen
import com.github.dwursteisen.minigdx.WorldResolution
import com.github.dwursteisen.minigdx.buffer.Buffer
import com.github.dwursteisen.minigdx.buffer.DataSource
import com.github.dwursteisen.minigdx.file.TextureImage
import com.github.dwursteisen.minigdx.gl
import com.github.dwursteisen.minigdx.shaders.Shader
import com.github.dwursteisen.minigdx.shaders.ShaderProgram
import com.github.dwursteisen.minigdx.shaders.TextureReference
import com.github.dwursteisen.minigdx.shaders.Uniform
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FillViewportStrategyTest {

    private val fillViewportStrategy = FillViewportStrategy()

    data class ViewportCall(val x: Int, val y: Int, val width: Int, val height: Int)

    private lateinit var viewportCall: ViewportCall

    @BeforeTest
    fun setUp() {
        gl = object : GL {
            override val screen: Screen
                get() = TODO("not implemented") // To change initializer of created properties use File | Settings | File Templates.

            override fun clearColor(r: Percent, g: Percent, b: Percent, a: Percent) {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun clear(mask: ByteMask) {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun clearDepth(depth: Number) {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun enable(mask: ByteMask) {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun blendFunc(sfactor: ByteMask, dfactor: ByteMask) {
                TODO("Not yet implemented")
            }

            override fun createProgram(): ShaderProgram {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun getAttribLocation(shaderProgram: ShaderProgram, name: String): Int {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun getUniformLocation(shaderProgram: ShaderProgram, name: String): Uniform {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun attachShader(shaderProgram: ShaderProgram, shader: Shader) {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun linkProgram(shaderProgram: ShaderProgram) {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun getProgramParameter(shaderProgram: ShaderProgram, mask: ByteMask): Any {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun getShaderParameter(shader: Shader, mask: ByteMask): Any {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun createShader(type: ByteMask): Shader {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun shaderSource(shader: Shader, source: String) {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun compileShader(shader: Shader) {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun getShaderInfoLog(shader: Shader): String {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun deleteShader(shader: Shader) {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun getProgramInfoLog(shader: ShaderProgram): String {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun createBuffer(): Buffer {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun bindBuffer(target: ByteMask, buffer: Buffer) {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun bufferData(target: ByteMask, data: DataSource, usage: Int) {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun depthFunc(target: ByteMask) {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun vertexAttribPointer(
                index: Int,
                size: Int,
                type: Int,
                normalized: Boolean,
                stride: Int,
                offset: Int
            ) {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun enableVertexAttribArray(index: Int) {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun useProgram(shaderProgram: ShaderProgram) {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun createTexture(): TextureReference {
                TODO("Not yet implemented")
            }

            override fun bindTexture(target: Int, textureReference: TextureReference) {
                TODO("Not yet implemented")
            }

            override fun uniformMatrix4fv(uniform: Uniform, transpose: Boolean, data: Array<Float>) {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun uniform1i(uniform: Uniform, data: Int) {
                TODO("Not yet implemented")
            }

            override fun uniform2f(uniform: Uniform, first: Float, second: Float) {
                TODO("Not yet implemented")
            }

            override fun drawArrays(mask: ByteMask, offset: Int, vertexCount: Int) {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun drawElements(mask: ByteMask, vertexCount: Int, type: Int, offset: Int) {
                TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
            }

            override fun viewport(x: Int, y: Int, width: Int, height: Int) {
                viewportCall = ViewportCall(x, y, width, height)
            }

            override fun texImage2D(
                target: Int,
                level: Int,
                internalformat: Int,
                format: Int,
                type: Int,
                source: TextureImage
            ) {
                TODO("Not yet implemented")
            }

            override fun texParameteri(target: Int, paramNam: Int, paramValue: Int) {
                TODO("Not yet implemented")
            }

            override fun generateMipmap(target: Int) {
                TODO("Not yet implemented")
            }
        }
    }

    @Test
    fun updateSquareWorldSquareScreen() {
        val world = WorldResolution(200, 200)
        fillViewportStrategy.update(world, 800, 800)
        assertEquals(viewportCall, ViewportCall(0, 0, 800, 800))
    }

    @Test
    fun updateSquareWorldHorizontalScreen() {
        val world = WorldResolution(200, 200)
        fillViewportStrategy.update(world, 800, 400)
        assertEquals(viewportCall, ViewportCall(200, 0, 400, 400))
    }

    @Test
    fun updateSquareWorldVerticalScreen() {
        val world = WorldResolution(200, 200)
        fillViewportStrategy.update(world, 400, 800)
        assertEquals(viewportCall, ViewportCall(0, 200, 400, 400))
    }

    @Test
    fun updateHorizontalWorldSquareScreen() {
        val world = WorldResolution(200, 100)
        fillViewportStrategy.update(world, 800, 800)
        assertEquals(viewportCall, ViewportCall(0, 200, 800, 400))
    }

    @Test
    fun updateHorizontalWorldHorizontalScreen() {
        val world = WorldResolution(400, 100)
        fillViewportStrategy.update(world, 800, 400)
        assertEquals(viewportCall, ViewportCall(0, 100, 800, 200))
    }

    @Test
    fun updateHorizontalWorldVerticalScreen() {
        val world = WorldResolution(200, 100)
        fillViewportStrategy.update(world, 400, 800)
        assertEquals(viewportCall, ViewportCall(0, 300, 400, 200))
    }
}
