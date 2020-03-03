package com.github.dwursteisen.minigdx.input

import android.view.MotionEvent
import android.view.View
import com.github.dwursteisen.minigdx.math.Vector2

class AndroidInputHandler : InputHandler, InputManager, View.OnTouchListener {

    override fun isKey(key: Key): Boolean = false

    override fun isKeyPressed(key: Key): Boolean = false

    private val touchSignalCache = TouchSignal.values()

    private val type = Array<Int?>(TouchSignal.values().size) { null }
    private val touch = Array<Vector2?>(TouchSignal.values().size) { null }
    private val justTouch = Array<Vector2?>(TouchSignal.values().size) { null }

    override fun isTouched(signal: TouchSignal): Vector2? {
        return touch[signal.ordinal]
    }

    override fun isJustTouched(signal: TouchSignal): Vector2? {
        return justTouch[signal.ordinal]
    }

    private fun lookForTouchSignal(pointerId: Int): TouchSignal? {
        return type.mapIndexed { index, id ->
            if (id == pointerId) {
                touchSignalCache[index]
            } else {
                null
            }
        }.filterNotNull()
            .firstOrNull()
    }

    private fun assignTouchSignal(pointerId: Int): TouchSignal {
        val ordinal = type.indexOfFirst { it == null }
        val touchSignal = touchSignalCache[ordinal]
        type[ordinal] = pointerId
        return touchSignal
    }

    private fun releaseTouchSignal(touchSignal: TouchSignal) {
        type[touchSignal.ordinal] = null
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val numberPointer = event.pointerCount
        val pointerIds = (0 until numberPointer).map {
            val pointerId = event.getPointerId(it)
            pointerId to lookForTouchSignal(pointerId)
        }

        pointerIds.forEach {
            val (pointerId, touchSignal) = it
            if (touchSignal == null) {
                val newTouchSignal = assignTouchSignal(pointerId)
                onTouch(newTouchSignal, event)
            } else {
                onTouch(touchSignal, event)
            }
        }

        return true
    }

    private fun onTouch(touchSignal: TouchSignal, event: MotionEvent) {
        val ordinal = touchSignal.ordinal
        when (event.action and MotionEvent.ACTION_POINTER_INDEX_MASK) {
            MotionEvent.ACTION_DOWN -> {
                // down
                val position = touch[ordinal] ?: Vector2(0, 0)
                position.x = event.getX(type[ordinal]!!)
                position.y = event.getY(type[ordinal]!!)
                touch[ordinal] = position
                justTouch[ordinal] = position
            }
            MotionEvent.ACTION_UP -> {
                // up
                touch[ordinal] = null
                justTouch[ordinal] = null
                releaseTouchSignal(touchSignal)
            }
            MotionEvent.ACTION_MOVE -> {
                // update
                touch[ordinal]?.x = event.getX(type[ordinal]!!)
                touch[ordinal]?.y = event.getY(type[ordinal]!!)
            }
        }
    }

    override fun record() = Unit

    override fun reset() {
        (justTouch.indices).forEach { i ->
            justTouch[i] = null
        }
    }
}
