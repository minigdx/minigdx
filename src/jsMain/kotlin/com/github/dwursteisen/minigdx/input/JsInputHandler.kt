package com.github.dwursteisen.minigdx.input

import com.github.dwursteisen.minigdx.math.Vector2
import kotlin.browser.document
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent

class JsInputHandler : InputHandler, InputManager {

    init {
        document.addEventListener("keydown", ::keyDown, false)
        document.addEventListener("keyup", ::keyUp, false)
    }

    private val keys: Array<Boolean> = Array(256 + 1) { false }
    private val pressed: Array<Boolean> = Array(256 + 1) { false }

    private fun keyDown(event: Event) {
        event as KeyboardEvent
        if (event.keyCode in (0..256)) {
            keys[event.keyCode] = true
            pressed[event.keyCode] = true
        }
    }

    private fun keyUp(event: Event) {
        event as KeyboardEvent
        if (event.keyCode in (0..256)) {
            keys[event.keyCode] = false
        }
    }

    override fun isKey(key: Key): Boolean = keys[key.keyCode]

    override fun isKeyPressed(key: Key): Boolean = pressed[key.keyCode]

    override fun isTouched(signal: TouchSignal): Vector2? = null

    override fun isJustTouched(signal: TouchSignal): Vector2? = null

    override fun record() = Unit

    override fun reset() {
        (pressed.indices).forEach {
            pressed[it] = false
        }
    }
}
