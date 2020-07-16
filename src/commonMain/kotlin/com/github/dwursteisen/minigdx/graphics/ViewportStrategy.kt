package com.github.dwursteisen.minigdx.graphics

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.WorldResolution
import kotlin.math.min

interface ViewportStrategy {

    fun update(gl: GL, worldResolution: WorldResolution, width: Int, height: Int)
}

class FillViewportStrategy : ViewportStrategy {

    override fun update(gl: GL, worldResolution: WorldResolution, width: Int, height: Int) {
        // Fill strategy.
        val sw = width
        val sh = height
        val ww = worldResolution.width.toFloat()
        val wh = worldResolution.height.toFloat()

        val ref = min(sw / ww, sh / wh)
        val pw = ww * ref
        val ph = wh * ref

        val gx = (sw - pw) * 0.5f
        val gy = (sh - ph) * 0.5f

        gl.viewport(gx.toInt(), gy.toInt(), pw.toInt(), ph.toInt())
    }
}
