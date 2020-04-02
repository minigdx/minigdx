package com.github.dwursteisen.minigdx.graphics

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.gl

fun Game.clear(r: Number, g: Number, b: Float) {
    gl.clearColor(r, g, b, 1f)
    gl.clearDepth(1.0)
    gl.enable(GL.DEPTH_TEST)
    gl.depthFunc(GL.LEQUAL)
    gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)
}
