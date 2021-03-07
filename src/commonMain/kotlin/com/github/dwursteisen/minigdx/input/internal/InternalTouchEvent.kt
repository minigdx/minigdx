package com.github.dwursteisen.minigdx.input.internal

import com.github.dwursteisen.minigdx.input.TouchSignal
import com.github.dwursteisen.minigdx.math.Vector2

class InternalTouchEvent(
    var touchSignal: TouchSignal = TouchSignal.TOUCH1,
    var position: Vector2 = Vector2(0f, 0f),
    var way: InternalTouchEventWay = InternalTouchEventWay.DOWN
)
