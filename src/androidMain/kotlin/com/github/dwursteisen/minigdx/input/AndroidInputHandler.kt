package com.github.dwursteisen.minigdx.input

import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import com.github.dwursteisen.minigdx.math.Vector2

class AndroidInputHandler : InputHandler, InputManager, View.OnTouchListener {

    private val touchManager = TouchManager(KeyEvent.KEYCODE_PROFILE_SWITCH)

    override fun isKeyJustPressed(key: Key): Boolean = if (key == Key.ANY_KEY) {
        touchManager.isAnyKeyJustPressed
    } else {
        touchManager.isKeyJustPressed(key.keyCode)
    }

    override fun isKeyPressed(key: Key): Boolean = if (key == Key.ANY_KEY) {
        touchManager.isAnyKeyPressed
    } else {
        touchManager.isKeyPressed(key.keyCode)
    }

    override fun isTouched(signal: TouchSignal) = touchManager.isTouched(signal)

    override fun isJustTouched(signal: TouchSignal): Vector2? = touchManager.isJustTouched(signal)

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val numberPointer = event.pointerCount
        (0 until numberPointer).map {
            val pointerId = event.getPointerId(it)
            val touch = touchManager.getTouchSignal(pointerId)
            onTouch(touch, pointerId, event)
        }

        return true
    }

    private fun onTouch(touchSignal: TouchSignal, pointerId: Int, event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // down
                touchManager.onTouchDown(touchSignal, event.getX(pointerId), event.getY(pointerId))
            }
            MotionEvent.ACTION_UP -> {
                // up
                touchManager.onTouchUp(touchSignal)
            }
            MotionEvent.ACTION_MOVE -> {
                // update
                touchManager.onTouchMove(touchSignal, event.getX(pointerId), event.getY(pointerId))
            }
        }
    }

    fun onKeyDown(keyCode: Int) {
        touchManager.onKeyPressed(keyCode)
    }

    fun onKeyUp(keyCode: Int) {
        touchManager.onKeyReleased(keyCode)
    }

    override fun record() = Unit

    override fun reset() {
        touchManager.processReceivedEvent()
    }
}
