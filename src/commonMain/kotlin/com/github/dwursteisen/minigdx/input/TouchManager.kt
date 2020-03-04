package com.github.dwursteisen.minigdx.input

import com.github.dwursteisen.minigdx.math.Vector2

class TouchManager {

    private val touchSignalCache = TouchSignal.values()

    private val type = Array<Any?>(TouchSignal.values().size) { null }
    private val touch = Array<Vector2?>(TouchSignal.values().size) { null }
    private val justTouch = Array<Vector2?>(TouchSignal.values().size) { null }

    fun getTouchSignal(id: Any): TouchSignal {
        type.forEachIndexed { index, savedId ->
            if (id == savedId) {
                return touchSignalCache[index]
            }
        }
        return assignTouchSignal(id)
    }

    private fun assignTouchSignal(id: Any): TouchSignal {
        val ordinal = type.indexOfFirst { it == null }
        val touchSignal = touchSignalCache[ordinal]
        type[ordinal] = id
        return touchSignal
    }

    fun onTouchDown(touchSignal: TouchSignal, x: Float, y: Float) {
        val ordinal = touchSignal.ordinal
        val position = touch[ordinal] ?: Vector2(0, 0)
        position.x = x
        position.y = y
        touch[ordinal] = position
        justTouch[ordinal] = position
    }

    fun onTouchMove(touchSignal: TouchSignal, x: Float, y: Float) {
        val ordinal = touchSignal.ordinal
        // update
        touch[ordinal]?.x = x
        touch[ordinal]?.y = y
    }

    fun onTouchUp(touchSignal: TouchSignal) {
        val ordinal = touchSignal.ordinal
        touch[ordinal] = null
        justTouch[ordinal] = null
        type[touchSignal.ordinal] = null
    }

    fun isTouched(touchSignal: TouchSignal): Vector2? {
        return touch[touchSignal.ordinal]
    }

    fun isJustTouched(touchSignal: TouchSignal): Vector2? {
        return justTouch[touchSignal.ordinal]
    }

    fun reset() {
        (justTouch.indices).forEach { i ->
            justTouch[i] = null
        }
    }
}
