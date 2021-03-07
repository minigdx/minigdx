package com.github.dwursteisen.minigdx

import kotlin.test.Test
import kotlin.test.assertEquals

class GameScreenConfigurationTest {

    @Test
    fun gameScreenWithResolutionReturnResolution() {
        val gameScreen = GameScreenConfiguration.WithResolution(1200, 900)
        val resolution = gameScreen.screen(1200, 900)
        assertEquals(1200, resolution.width)
        assertEquals(900, resolution.height)
    }

    @Test
    fun gameScreenWithResolutionReturnRatio() {
        val gameScreen = GameScreenConfiguration.WithResolution(16, 9)
        val ratio = gameScreen.screen(1200, 900)
        assertEquals(16 / 9f, ratio.ratio)
    }

    @Test
    fun gameScreenWithRatioReturnResolutionSquare() {
        val gameScreen = GameScreenConfiguration.WithRatio(1f)
        val resolution = gameScreen.screen(1200, 900)
        assertEquals(900, resolution.width)
        assertEquals(900, resolution.height)
    }

    @Test
    fun gameScreenWithRatioReturnResolutionRectangle() {
        val gameScreen = GameScreenConfiguration.WithRatio(10f / 100f)
        val resolution = gameScreen.screen(100, 100)
        assertEquals(10, resolution.width)
        assertEquals(100, resolution.height)
    }

    @Test
    fun gameScreenWithRatioReturnRatio() {
        val gameScreen = GameScreenConfiguration.WithRatio(1f)
        val ratio = gameScreen.screen(1200, 900)
        assertEquals(1f, ratio.ratio)
    }

    @Test
    fun gameScreenWithCurrentScreenResolutionReturnResolution() {
        val gameScreen = GameScreenConfiguration.WithCurrentScreenResolution()
        val resolution = gameScreen.screen(1200, 900)
        assertEquals(1200, resolution.width)
        assertEquals(900, resolution.height)
    }

    @Test
    fun gameScreenWithCurrentScreenResolutionReturnRatio() {
        val gameScreen = GameScreenConfiguration.WithCurrentScreenResolution()
        val ratio = gameScreen.screen(16, 9)
        assertEquals(16 / 9f, ratio.ratio)
    }
}
