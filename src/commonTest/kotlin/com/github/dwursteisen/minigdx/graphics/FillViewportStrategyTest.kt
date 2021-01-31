package com.github.dwursteisen.minigdx.graphics

import MockLogger
import com.github.dwursteisen.minigdx.Screen
import kotlin.test.Test
import kotlin.test.assertEquals

class FillViewportStrategyTest {

    private val fillViewportStrategy = FillViewportStrategy(MockLogger())

    @Test
    fun updateSquareWorldSquareScreen() {
        val gl = MockGL(Screen(200, 200))
        fillViewportStrategy.update(gl, 800, 800)
        assertEquals(gl.viewportCall, ViewportCall(0, 0, 800, 800))
    }

    @Test
    fun updateSquareWorldHorizontalScreen() {
        val gl = MockGL(Screen(200, 200))
        fillViewportStrategy.update(gl, 800, 400)
        assertEquals(gl.viewportCall, ViewportCall(200, 0, 400, 400))
    }

    @Test
    fun updateSquareWorldVerticalScreen() {
        val gl = MockGL(Screen(200, 200))
        fillViewportStrategy.update(gl, 400, 800)
        assertEquals(gl.viewportCall, ViewportCall(0, 200, 400, 400))
    }

    @Test
    fun updateHorizontalWorldSquareScreen() {
        val gl = MockGL(Screen(200, 100))
        fillViewportStrategy.update(gl, 800, 800)
        assertEquals(gl.viewportCall, ViewportCall(0, 200, 800, 400))
    }

    @Test
    fun updateHorizontalWorldHorizontalScreen() {
        val gl = MockGL(Screen(400, 100))
        fillViewportStrategy.update(gl, 800, 400)
        assertEquals(gl.viewportCall, ViewportCall(0, 100, 800, 200))
    }

    @Test
    fun updateHorizontalWorldVerticalScreen() {
        val gl = MockGL(Screen(200, 100))
        fillViewportStrategy.update(gl, 400, 800)
        assertEquals(gl.viewportCall, ViewportCall(0, 300, 400, 200))
    }
}
