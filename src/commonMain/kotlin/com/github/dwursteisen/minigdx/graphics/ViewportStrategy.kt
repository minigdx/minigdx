package com.github.dwursteisen.minigdx.graphics

import com.github.dwursteisen.minigdx.DevicePosition
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.GamePosition
import com.github.dwursteisen.minigdx.Pixel
import com.github.dwursteisen.minigdx.Resolution
import com.github.dwursteisen.minigdx.logger.Logger
import kotlin.math.min
import kotlin.math.roundToInt

interface ViewportStrategy {

    /**
     * Update the viewport (rendered area) for the actual screen
     * having a resolutions oy [width] pixels per [height] pixels.
     *
     * The [width] and [height] has to be the size of the framebuffer.
     */
    fun update(gl: GL, width: Pixel, height: Pixel, gameWidth: Pixel, gameHeight: Pixel)

    fun update(
        gl: GL,
        frameBufferScreen: Resolution,
        gameScreen: Resolution
    ) = update(
        gl,
        frameBufferScreen.width,
        frameBufferScreen.height,
        gameScreen.width,
        gameScreen.height
    )

    /**
     * Convert a device position into a game position
     *
     * @return the game position. Can be negative or superior to game screen size if
     *         out of the game viewport.
     */
    fun convert(
        deviceX: DevicePosition,
        deviceY: DevicePosition,
        // device resolution
        width: Pixel,
        height: Pixel,
        // game resolution
        gameWidth: Pixel,
        gameHeight: Pixel
    ): Pair<GamePosition, GamePosition>
}

/**
 * Strategy to fill the screen while keeping the ratio of the requested screen.
 *
 */
class FillViewportStrategy(private val logger: Logger) : ViewportStrategy {

    override fun update(gl: GL, width: Pixel, height: Pixel, gameWidth: Pixel, gameHeight: Pixel) {
        apply(width, height, gameWidth, gameHeight) { x, y, w, h ->
            gl.viewport(x, y, w, h)
        }
    }

    override fun convert(
        deviceX: DevicePosition,
        deviceY: DevicePosition,
        width: Pixel,
        height: Pixel,
        gameWidth: Pixel,
        gameHeight: Pixel
    ): Pair<GamePosition, GamePosition> {
        return apply(width, height, gameWidth, gameHeight) { x, y, w, h ->

            val xOrigin = deviceX - x.toFloat()
            val yOrigin = deviceY - y.toFloat()

            val gameX = xOrigin * gameWidth.toFloat() / w.toFloat()
            val gameY = (yOrigin * gameHeight.toFloat()) / h.toFloat()

            gameX to gameY
        }
    }

    private fun <T> apply(
        width: Pixel,
        height: Pixel,
        gameWidth: Pixel,
        gameHeight: Pixel,
        block: (x: Int, y: Int, width: Int, height: Int) -> T
    ): T {
        // Fill strategy.
        val sw = width
        val sh = height
        val ww = gameWidth.toFloat()
        val wh = gameHeight.toFloat()

        val ref = min(sw / ww, sh / wh)
        val pw = ww * ref
        val ph = wh * ref

        val gx = (sw - pw) * 0.5f
        val gy = (sh - ph) * 0.5f

        val x = gx.roundToInt()
        val y = gy.roundToInt()
        val w = pw.roundToInt()
        val h = ph.roundToInt()

        return block(x, y, w, h)
    }
}
