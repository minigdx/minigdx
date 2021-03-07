package com.github.dwursteisen.minigdx.input

import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import com.github.dwursteisen.minigdx.math.Vector2

class AndroidInputHandler : InputHandler, InputManager, View.OnTouchListener {

    private val touchManager = TouchManager()

    private var frame: Long = Long.MIN_VALUE + 1

    private val keysJustPressed = LongArray(KeyEvent.KEYCODE_PROFILE_SWITCH)
    private val keysJustRelease = BooleanArray(KeyEvent.KEYCODE_PROFILE_SWITCH)
    private val keysPressed = BooleanArray(KeyEvent.KEYCODE_PROFILE_SWITCH)

    override fun isKeyJustPressed(key: Key): Boolean = keysJustPressed[key.keyCode] == frame - 1

    override fun isKeyPressed(key: Key): Boolean = keysPressed[key.keyCode]

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
        keysPressed[keyCode] = true
        keysJustPressed[keyCode] = frame
        keysJustRelease[keyCode] = false
    }

    fun onKeyUp(keyCode: Int) {
        keysPressed[keyCode] = false
        keysJustRelease[keyCode] = true
    }

    override fun record() = Unit

    override fun reset() {
        touchManager.processReceivedEvent()
        frame++
    }
}
