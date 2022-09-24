package com.github.dwursteisen.minigdx.imgui

import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.input.TouchSignal
import com.github.minigdx.imgui.InputCapture

class ImGuiInputCapture(val input: InputHandler) : InputCapture {

    override val x: Float
        get() = input.currentTouch.x

    override val y: Float
        get() = input.currentTouch.y

    override val isTouch: Boolean
        get() = input.isJustTouched(TouchSignal.TOUCH1) != null
}
