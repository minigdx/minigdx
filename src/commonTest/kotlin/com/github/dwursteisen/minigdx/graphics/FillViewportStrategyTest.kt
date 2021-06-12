package com.github.dwursteisen.minigdx.graphics

import MockLogger
import kotlin.test.Test
import kotlin.test.assertEquals
import com.github.dwursteisen.minigdx.ecs.components.assertEquals as assertEqualsF

class FillViewportStrategyTest {

    private val fillViewportStrategy = FillViewportStrategy(MockLogger())

    private val gl = MockGL()

    @Test
    fun updateSquareWorldSquareScreen() {
        fillViewportStrategy.update(gl, 800, 800, 200, 200)
        assertEquals(gl.viewportCall, ViewportCall(0, 0, 800, 800))
    }

    @Test
    fun updateSquareWorldHorizontalScreen() {
        fillViewportStrategy.update(gl, 800, 400, 200, 200)
        assertEquals(gl.viewportCall, ViewportCall(200, 0, 400, 400))
    }

    @Test
    fun updateSquareWorldVerticalScreen() {
        fillViewportStrategy.update(gl, 400, 800, 200, 200)
        assertEquals(gl.viewportCall, ViewportCall(0, 200, 400, 400))
    }

    @Test
    fun updateHorizontalWorldSquareScreen() {
        fillViewportStrategy.update(gl, 800, 800, 200, 100)
        assertEquals(gl.viewportCall, ViewportCall(0, 200, 800, 400))
    }

    @Test
    fun updateHorizontalWorldHorizontalScreen() {
        fillViewportStrategy.update(gl, 800, 400, 400, 100)
        assertEquals(gl.viewportCall, ViewportCall(0, 100, 800, 200))
    }

    @Test
    fun updateHorizontalWorldVerticalScreen() {
        fillViewportStrategy.update(gl, 400, 800, 200, 100)
        assertEquals(gl.viewportCall, ViewportCall(0, 300, 400, 200))
    }

    @Test
    fun convertSquareDeviceScreenSquareGameScreen() {
        val (x, y) = fillViewportStrategy.convert(
            0f,
            0f,
            100,
            100,
            100,
            100
        )

        assertEqualsF(0f, x)
        assertEqualsF(0f, y)
    }

    @Test
    fun convertHorizontalDeviceScreenSquareGameScreenTopLeftDeviseScreen() {
        val (x, y) = fillViewportStrategy.convert(
            0f,
            0f,
            100,
            50,
            100,
            100
        )

        assertEqualsF(-50f, x)
        assertEqualsF(0f, y)
    }

    @Test
    fun convertHorizontalDeviceScreenSquareGameScreenTopLeftGameScreen() {
        val (x, y) = fillViewportStrategy.convert(
            25f,
            0f,
            100,
            50,
            100,
            100
        )

        assertEqualsF(0f, x)
        assertEqualsF(0f, y)
    }

    @Test
    fun convertHorizontalDeviceScreenSquareGameScreenTopRightGameScreen() {
        val (x, y) = fillViewportStrategy.convert(
            75f,
            0f,
            100,
            50,
            100,
            100
        )

        assertEqualsF(100f, x)
        assertEqualsF(0f, y)
    }

    @Test
    fun convertSquareDeviceScreenVerticalGameScreenTopLeftDeviseScreen() {
        val (x, y) = fillViewportStrategy.convert(
            0f,
            0f,
            100,
            100,
            500,
            1000
        )

        assertEqualsF(-250f, x)
        assertEqualsF(0f, y)
    }
}
