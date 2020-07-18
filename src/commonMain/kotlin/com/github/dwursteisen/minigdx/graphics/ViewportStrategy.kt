package com.github.dwursteisen.minigdx.graphics

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.logger.Logger
import kotlin.math.min

interface ViewportStrategy {

    fun update(gl: GL, width: Int, height: Int)
}

class FillViewportStrategy(private val logger: Logger) : ViewportStrategy {

    override fun update(gl: GL, width: Int, height: Int) {
        // Fill strategy.
        val sw = width
        val sh = height
        val ww = gl.screen.width.toFloat()
        val wh = gl.screen.height.toFloat()

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
