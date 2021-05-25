package com.github.dwursteisen.minigdx.input

import com.github.dwursteisen.minigdx.input.TouchSignal.TOUCH1
import com.github.dwursteisen.minigdx.input.TouchSignal.TOUCH2
import com.github.dwursteisen.minigdx.input.TouchSignal.TOUCH3
import com.github.dwursteisen.minigdx.math.Vector2
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.TouchEvent
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.get
import kotlin.experimental.and

class JsInputHandler(private val canvas: HTMLCanvasElement) : InputHandler, InputManager {

    init {
        document.addEventListener("keydown", ::keyDown, false)
        document.addEventListener("keyup", ::keyUp, false)
        canvas.addEventListener("touchstart", ::touchStart, false)
        canvas.addEventListener("touchend", ::touchEnd, false)
        canvas.addEventListener("touchmove", ::touchMove, false)
        canvas.addEventListener("mousedown", ::mouseDown, false)
        canvas.addEventListener("mouseup", ::mouseUp, false)
        canvas.addEventListener("mousemove", ::mouseMove, false)
        canvas.addEventListener("mouseleave", ::mouseLeave, false)
        canvas.addEventListener("mouseenter", ::mouseEnter, false)
    }

    private val flagMouse1: Short = 0x1
    private val flagMouse2: Short = 0x10
    private val flagMouse3: Short = 0x100
    private val flags = arrayOf(flagMouse1, flagMouse2, flagMouse3)
    private val touchSignals = arrayListOf<TouchSignal>(
        TOUCH1,
        TOUCH2,
        TOUCH3
    )
    private val touchManager = TouchManager(UNKNOWN_KEY)

    private var isMouseInsideCanvas: Boolean = false
    private var mousePosition: Vector2 = Vector2(0, 0)

    private fun mouseDown(event: Event) {
        event as MouseEvent
        val jsTouch = event.buttons
        flags.forEachIndexed { index, flag ->
            if (jsTouch.and(flag) == flag) {
                val touch = touchSignals[index]
                touchManager.onTouchDown(touch, event.clientX.toFloat(), event.clientY.toFloat())
            }
        }
    }

    private fun mouseUp(event: Event) {
        event as MouseEvent
        touchSignals.forEach { touch ->
            touchManager.onTouchUp(touch)
        }
    }

    private fun mouseMove(event: Event) {
        event as MouseEvent
        val jsTouch = event.buttons
        flags.forEachIndexed { index, flag ->
            if (jsTouch.and(flag) == flag) {
                val touch = touchSignals[index]
                touchManager.onTouchMove(touch, event.clientX.toFloat(), event.clientY.toFloat())
            }
        }

        if (isMouseInsideCanvas) {
            val rect = canvas.getBoundingClientRect()
            mousePosition.x = event.clientX.toFloat() - rect.left.toFloat()
            mousePosition.y = event.clientY.toFloat() - rect.top.toFloat()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun mouseLeave(event: Event) {
        isMouseInsideCanvas = false
    }

    @Suppress("UNUSED_PARAMETER")
    private fun mouseEnter(event: Event) {
        isMouseInsideCanvas = true
    }

    private fun touchStart(event: Event) {
        event as TouchEvent
        (0 until event.targetTouches.length).forEach {
            val jsTouch = event.targetTouches[it]!!
            val touch = touchManager.getTouchSignal(jsTouch.identifier)
            touchManager.onTouchDown(touch, jsTouch.pageX.toFloat(), jsTouch.pageY.toFloat())
        }
    }

    private fun touchEnd(event: Event) {
        event as TouchEvent
        (0 until event.targetTouches.length).forEach {
            val jsTouch = event.targetTouches[it]!!
            val touch = touchManager.getTouchSignal(jsTouch.identifier)
            touchManager.onTouchUp(touch)
        }
    }

    private fun touchMove(event: Event) {
        event as TouchEvent
        (0 until event.targetTouches.length).forEach {
            val jsTouch = event.targetTouches[it]!!
            val touch = touchManager.getTouchSignal(jsTouch.identifier)
            touchManager.onTouchMove(touch, jsTouch.pageX.toFloat(), jsTouch.pageY.toFloat())
        }
    }

    private fun keyDown(event: Event) {
        event as KeyboardEvent
        if (event.keyCode in (0..256)) {
            touchManager.onKeyPressed(event.keyCode)
            event.preventDefault()
        }
    }

    private fun keyUp(event: Event) {
        event as KeyboardEvent
        if (event.keyCode in (0..256)) {
            touchManager.onKeyReleased(event.keyCode)
            event.preventDefault()
        }
    }

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

    override fun isTouched(signal: TouchSignal): Vector2? = touchManager.isTouched(signal)

    override fun isJustTouched(signal: TouchSignal): Vector2? = touchManager.isJustTouched(signal)

    override fun touchIdlePosition(): Vector2? {
        return if (isMouseInsideCanvas) {
            mousePosition
        } else {
            null
        }
    }

    override fun record() = Unit

    override fun reset() {
        touchManager.processReceivedEvent()
    }
}
