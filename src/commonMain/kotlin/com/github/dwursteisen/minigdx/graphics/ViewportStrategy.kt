package com.github.dwursteisen.minigdx.graphics

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.Pixel
import com.github.dwursteisen.minigdx.Resolution
import com.github.dwursteisen.minigdx.logger.Logger
import kotlin.math.min

interface ViewportStrategy {

    /**
     * Update the viewport (rendered area) for the actual screen
     * having a resolutions oy [width] pixels per [height] pixels.
     */
    fun update(gl: GL, width: Pixel, height: Pixel, gameWidth: Pixel, gameHeight: Pixel)

    fun update(
        gl: GL,
        deviceScreen: Resolution,
        gameScreen: Resolution
    ) = update(
        gl,
        deviceScreen.width,
        deviceScreen.height,
        gameScreen.width,
        gameScreen.height
    )
}

/**
 * Strategy to fill the screen while keeping the ratio of the requested screen.
 *
 */
class FillViewportStrategy(private val logger: Logger) : ViewportStrategy {

    override fun update(gl: GL, width: Pixel, height: Pixel, gameWidth: Pixel, gameHeight: Pixel) {
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

        val x = gx.toInt()
        val y = gy.toInt()
        val w = pw.toInt()
        val h = ph.toInt()

        logger.info("FILL_VIEWPORT_STRATEGY") {
            "Fill the screen '$width/$height' as a viewport '$w/$h' (offset: $x/$y)"
        }

        gl.viewport(x, y, w, h)
    }
}
