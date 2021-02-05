package com.github.dwursteisen.minigdx

sealed class GameScreenConfiguration {

    /**
     * Will keep the ratio and compute the associated resolution.
     */
    data class WithRatio(private val ratio: Ratio) : GameScreenConfiguration() {

        override fun screen(width: Pixel, height: Pixel): Resolution {
            return Resolution((height * ratio).toInt(), height)
        }
    }

    /**
     * Will keep the resolution and compute the associated ratio.
     */
    data class WithResolution(val width: Pixel, val height: Pixel) : GameScreenConfiguration() {

        private val resolution = Resolution(width, height)

        override fun screen(width: Pixel, height: Pixel): Resolution = resolution
    }

    /**
     * Will keep the resolution of the actual screen and compute the associated ratio.
     */
    class WithCurrentScreenResolution : GameScreenConfiguration() {

        override fun screen(width: Pixel, height: Pixel): Resolution = Resolution(width, height)
    }

    abstract fun screen(width: Pixel, height: Pixel): Resolution

    fun screen(resolution: Resolution): Resolution = screen(resolution.width, resolution.height)
}
