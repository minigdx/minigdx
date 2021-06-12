package com.github.dwursteisen.minigdx.imgui

import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.input.TouchSignal
import com.github.dwursteisen.minigdx.math.Vector2
import com.github.minigdx.imgui.InputCapture

class ImgCapture(val input: () -> InputHandler) : InputCapture {

    private val pos: Vector2 = Vector2(-1f, -1f)
    private var touch = false

    override val x: Float
        get() = pos.x
    override val y: Float
        get() = pos.y
    override val isTouch: Boolean
        get() = touch

    override fun update() {
        val inputHandler = input()
        inputHandler.touchIdlePosition()?.run {
            pos.x = this.x
            pos.y = this.y
        }

        touch = false
        inputHandler.isJustTouched(TouchSignal.TOUCH1)?.run {
            pos.x = this.x
            pos.y = this.y
            touch = true
        }
    }
}
