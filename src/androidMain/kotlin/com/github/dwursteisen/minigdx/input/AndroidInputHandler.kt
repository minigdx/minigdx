package com.github.dwursteisen.minigdx.input

import android.view.MotionEvent
import android.view.View
import com.github.dwursteisen.minigdx.math.Vector2

class AndroidInputHandler : InputHandler, InputManager, View.OnTouchListener {

    override fun isKey(key: Key): Boolean = false

    override fun isKeyPressed(key: Key): Boolean = false

    private val touch = Array<Vector2?>(TouchSignal.values().size) { null }
    private val justTouch = Array<Vector2?>(TouchSignal.values().size) { null }

    override fun isTouched(signal: TouchSignal): Vector2? {
        return touch[signal.ordinal]
    }

    override fun isJustTouched(signal: TouchSignal): Vector2? {
        return justTouch[signal.ordinal]
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        // FIXME: one finger support only
        if (event.action == MotionEvent.ACTION_DOWN) {
            // down
            val position = touch[TouchSignal.FINGER1.ordinal] ?: Vector2(0, 0)
            position.x = event.x
            position.y = event.y
            touch[TouchSignal.FINGER1.ordinal] = position
            justTouch[TouchSignal.FINGER1.ordinal] = position
        } else if (event.action == MotionEvent.ACTION_UP) {
            // up
            touch[TouchSignal.FINGER1.ordinal] = null
            justTouch[TouchSignal.FINGER1.ordinal] = null
        } else if (event.action == MotionEvent.ACTION_MOVE) {
            // update
            touch[TouchSignal.FINGER1.ordinal]?.x = event.x
            touch[TouchSignal.FINGER1.ordinal]?.y = event.y
        }
        return true
    }

    override fun record() = Unit

    override fun reset() {
        (justTouch.indices).forEach { i ->
            justTouch[i] = null
        }
    }
}
