package com.github.dwursteisen.minigdx.input

import android.view.MotionEvent
import android.view.View
import com.github.dwursteisen.minigdx.math.Vector2

class AndroidInputHandler : InputHandler, InputManager, View.OnTouchListener {

    override fun isKey(key: Key): Boolean = false

    override fun isKeyPressed(key: Key): Boolean = false

    private val touchManager = TouchManager()

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
        val ordinal = touchSignal.ordinal
        when (event.action and MotionEvent.ACTION_POINTER_INDEX_MASK) {
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

    override fun record() = Unit

    override fun reset() {
        touchManager.reset()
    }
}
