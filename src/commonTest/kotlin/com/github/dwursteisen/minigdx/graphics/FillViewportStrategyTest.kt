package com.github.dwursteisen.minigdx.graphics

import com.github.dwursteisen.minigdx.WorldResolution
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FillViewportStrategyTest {

    private val fillViewportStrategy = FillViewportStrategy()

    private lateinit var gl: MockGL

    @BeforeTest
    fun setUp() {
        gl = MockGL()
    }

    @Test
    fun updateSquareWorldSquareScreen() {
        val world = WorldResolution(200, 200)
        fillViewportStrategy.update(gl, world, 800, 800)
        assertEquals(gl.viewportCall, ViewportCall(0, 0, 800, 800))
    }

    @Test
    fun updateSquareWorldHorizontalScreen() {
        val world = WorldResolution(200, 200)
        fillViewportStrategy.update(gl, world, 800, 400)
        assertEquals(gl.viewportCall, ViewportCall(200, 0, 400, 400))
    }

    @Test
    fun updateSquareWorldVerticalScreen() {
        val world = WorldResolution(200, 200)
        fillViewportStrategy.update(gl, world, 400, 800)
        assertEquals(gl.viewportCall, ViewportCall(0, 200, 400, 400))
    }

    @Test
    fun updateHorizontalWorldSquareScreen() {
        val world = WorldResolution(200, 100)
        fillViewportStrategy.update(gl, world, 800, 800)
        assertEquals(gl.viewportCall, ViewportCall(0, 200, 800, 400))
    }

    @Test
    fun updateHorizontalWorldHorizontalScreen() {
        val world = WorldResolution(400, 100)
        fillViewportStrategy.update(gl, world, 800, 400)
        assertEquals(gl.viewportCall, ViewportCall(0, 100, 800, 200))
    }

    @Test
    fun updateHorizontalWorldVerticalScreen() {
        val world = WorldResolution(200, 100)
        fillViewportStrategy.update(gl, world, 400, 800)
        assertEquals(gl.viewportCall, ViewportCall(0, 300, 400, 200))
    }
}
