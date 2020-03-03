package com.github.dwursteisen.minigdx.graphics

import com.github.dwursteisen.minigdx.WorldSize
import com.github.dwursteisen.minigdx.gl
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.BeforeTest
import kotlin.test.Test

class FillViewportStrategyTest {

    private val fillViewportStrategy = FillViewportStrategy()

    @BeforeTest
    fun setUp() {
        gl = mockk(relaxed = true)
    }

    @Test
    fun updateSquareWorldSquareScreen() {
        val world = WorldSize(200, 200)
        fillViewportStrategy.update(world, 800, 800)
        verify { gl.viewport(0, 0, 800, 800) }
    }

    @Test
    fun updateSquareWorldHorizontalScreen() {
        val world = WorldSize(200, 200)
        fillViewportStrategy.update(world, 800, 400)
        verify { gl.viewport(200, 0, 400, 400) }
    }

   @Test
    fun updateSquareWorldVerticalScreen() {
        val world = WorldSize(200, 200)
        fillViewportStrategy.update(world, 400, 800)
        verify { gl.viewport(0, 200, 400, 400) }
    }

    @Test
    fun updateHorizontalWorldSquareScreen() {
        val world = WorldSize(200, 100)
        fillViewportStrategy.update(world, 800, 800)
        verify { gl.viewport(0, 200, 800, 400) }
    }

    @Test
    fun updateHorizontalWorldHorizontalScreen() {
        val world = WorldSize(400, 100)
        fillViewportStrategy.update(world, 800, 400)
        verify { gl.viewport(0, 100, 800, 200) }
    }

    @Test
    fun updateHorizontalWorldVerticalScreen() {
        val world = WorldSize(200, 100)
        fillViewportStrategy.update(world, 400, 800)
        verify { gl.viewport(0, 300, 400, 200) }
    }
}
