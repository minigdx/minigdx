package com.github.dwursteisen.minigdx.graphics

import com.github.dwursteisen.minigdx.WorldSize
import com.github.dwursteisen.minigdx.gl
import kotlin.math.min

interface ViewportStrategy {

    fun update(worldSize: WorldSize, width: Int, height: Int)
}

class FillViewportStrategy : ViewportStrategy {

    override fun update(worldSize: WorldSize, width: Int, height: Int) {
        // Fill strategy.
        val sw = width
        val sh = height
        val ww = worldSize.width.toFloat()
        val wh = worldSize.height.toFloat()

        val ref = min(sw / ww, sh / wh)
        val pw = ww * ref
        val ph = wh * ref

        val gx = (sw - pw) * 0.5f
        val gy = (sh - ph) * 0.5f

        gl.viewport(gx.toInt(), gy.toInt(), pw.toInt(), ph.toInt())
    }
}
