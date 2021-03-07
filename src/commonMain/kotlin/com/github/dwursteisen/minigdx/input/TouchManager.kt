package com.github.dwursteisen.minigdx.input

import com.github.dwursteisen.minigdx.input.internal.InternalTouchEvent
import com.github.dwursteisen.minigdx.input.internal.InternalTouchEventWay
import com.github.dwursteisen.minigdx.math.Vector2
import com.github.dwursteisen.minigdx.utils.ObjectPool

class TouchManager {

    private val touchSignalCache = TouchSignal.values()

    private val type = Array<Any?>(TouchSignal.values().size) { null }
    private val touch = Array<Vector2?>(TouchSignal.values().size) { null }
    private val justTouch = Array<Vector2?>(TouchSignal.values().size) { null }

    private val eventsPool = InternalTouchEventObjectPool()

    inner class InternalTouchEventObjectPool : ObjectPool<InternalTouchEvent>(10) {
        override fun newInstance(): InternalTouchEvent {
            return InternalTouchEvent()
        }

        override fun destroyInstance(obj: InternalTouchEvent) = Unit
    }

    private val queueEvents = mutableListOf<InternalTouchEvent>()

    /**
     * Converting the platform specific touch [id] into a [TouchSignal].
     */
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

    /**
     * To be called when a touch has been detected on the platform.
     *
     * The platform touch needs to be converted using [getTouchSignal]
     * before being passed to [touchSignal].
     *
     * The coordinates [x] and [y] are screen coordinates.
     */
    fun onTouchDown(touchSignal: TouchSignal, x: Float, y: Float) {
        val event = eventsPool.newInstance()

        val ordinal = touchSignal.ordinal
        // Keep the position if the touch is currently touched
        val position = touch[ordinal] ?: event.position

        event.way = InternalTouchEventWay.DOWN
        event.touchSignal = touchSignal
        event.position = position.apply {
            position.x = x
            position.y = y
        }
        queueEvents.add(event)
    }

    /**
     * To be called when a touch move has been detected on the platform.
     *
     * The platform touch needs to be converted using [getTouchSignal]
     * before being passed to [touchSignal].
     *
     * The coordinates [x] and [y] are screen coordinates.
     */
    fun onTouchMove(touchSignal: TouchSignal, x: Float, y: Float) {
        val event = eventsPool.newInstance()
        event.way = InternalTouchEventWay.MOVE
        event.position.x = x
        event.position.y = y
        event.touchSignal = touchSignal
        queueEvents.add(event)
    }

    /**
     * To be called when a touch up has been detected on the platform.
     *
     * The platform touch needs to be converted using [getTouchSignal]
     * before being passed to [touchSignal].
     *
     * The coordinates [x] and [y] are screen coordinates.
     */
    fun onTouchUp(touchSignal: TouchSignal) {
        val event = eventsPool.newInstance()
        event.way = InternalTouchEventWay.UP
        event.touchSignal = touchSignal
        queueEvents.add(event)
    }

    fun isTouched(touchSignal: TouchSignal): Vector2? {
        return touch[touchSignal.ordinal]
    }

    fun isJustTouched(touchSignal: TouchSignal): Vector2? {
        return justTouch[touchSignal.ordinal]
    }

    /**
     * Process received touch events
     *
     * Previous received events will be discarded.
     */
    fun processReceivedEvent() {
        // Reset previous justTouch
        (justTouch.indices).forEach { i ->
            justTouch[i] = null
        }
        queueEvents.forEach { event ->
            when (event.way) {
                InternalTouchEventWay.DOWN -> {
                    justTouch[event.touchSignal.ordinal] = event.position
                    touch[event.touchSignal.ordinal] = event.position
                }
                InternalTouchEventWay.MOVE -> {
                    touch[event.touchSignal.ordinal]?.run {
                        x = event.position.x
                        y = event.position.y
                    }
                }
                InternalTouchEventWay.UP -> {
                    touch[event.touchSignal.ordinal] = null
                }
            }
        }
        queueEvents.clear()
    }
}
