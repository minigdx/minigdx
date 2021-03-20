package com.github.dwursteisen.minigdx.input.internal

import com.github.dwursteisen.minigdx.input.KeyCode
import com.github.dwursteisen.minigdx.input.TouchSignal
import com.github.dwursteisen.minigdx.math.Vector2

class InternalTouchEvent(
    /**
     * Key code is not null if the event is about a key change
     */
    var keycode: KeyCode? = null,
    /**
     * If the keycode is not null; it's a touch event
     */
    var touchSignal: TouchSignal = TouchSignal.TOUCH1,
    var position: Vector2 = Vector2(0f, 0f),
    var way: InternalTouchEventWay = InternalTouchEventWay.DOWN
) {
    val isTouchEvent: Boolean
        get() = keycode == null
}
