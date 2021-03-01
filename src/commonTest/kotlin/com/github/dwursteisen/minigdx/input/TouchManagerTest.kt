package com.github.dwursteisen.minigdx.input

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TouchManagerTest {

    @Test
    fun touch_down_then_is_just_touched_then_reset() {
        val touchManager = TouchManager()
        touchManager.onTouchDown(TouchSignal.TOUCH1, 0f, 0f)
        assertNull(touchManager.isJustTouched(TouchSignal.TOUCH1))
        touchManager.processReceivedEvent()
        assertNotNull(touchManager.isJustTouched(TouchSignal.TOUCH1))
        touchManager.processReceivedEvent()
        assertNull(touchManager.isJustTouched(TouchSignal.TOUCH1))
    }

    @Test
    fun touch_down_then_reset_then_is_just_touched() {
        val touchManager = TouchManager()
        touchManager.onTouchDown(TouchSignal.TOUCH1, 0f, 0f)
        touchManager.processReceivedEvent()
        assertNotNull(touchManager.isJustTouched(TouchSignal.TOUCH1))
        touchManager.processReceivedEvent()
        assertNull(touchManager.isJustTouched(TouchSignal.TOUCH1))
    }
}
