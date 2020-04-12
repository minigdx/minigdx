package com.github.dwursteisen.minigdx.input

import com.github.dwursteisen.minigdx.input.TouchSignal.TOUCH1
import com.github.dwursteisen.minigdx.input.TouchSignal.TOUCH2
import com.github.dwursteisen.minigdx.input.TouchSignal.TOUCH3
import com.github.dwursteisen.minigdx.math.Vector2
import kotlin.browser.document
import kotlin.experimental.and
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.TouchEvent
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.get

class JsInputHandler(canvas: HTMLCanvasElement) : InputHandler, InputManager {

    init {
        document.addEventListener("keydown", ::keyDown, false)
        document.addEventListener("keyup", ::keyUp, false)
        canvas.addEventListener("touchstart", ::touchStart, false)
        canvas.addEventListener("touchend", ::touchEnd, false)
        canvas.addEventListener("touchmove", ::touchMove, false)
        canvas.addEventListener("mousedown", ::mouseDown, false)
        canvas.addEventListener("mouseup", ::mouseUp, false)
        canvas.addEventListener("mousemove", ::mouseMove, false)
    }

    private val justPressed: Array<Boolean> = Array(256 + 1) { false }
    private val justRelease: Array<Boolean> = Array(256 + 1) { true }
    private val pressed: Array<Boolean> = Array(256 + 1) { false }

    private val flagMouse1: Short = 0x1
    private val flagMouse2: Short = 0x10
    private val flagMouse3: Short = 0x100
    private val flags = arrayOf(flagMouse1, flagMouse2, flagMouse3)
    private val touchSignals = arrayListOf<TouchSignal>(
        TOUCH1,
        TOUCH2,
        TOUCH3
    )
    private val touchManager = TouchManager()

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
            if (justRelease[event.keyCode]) {
                justPressed[event.keyCode] = true
                justRelease[event.keyCode] = false
            }
            pressed[event.keyCode] = true
            event.preventDefault()
        }
    }

    private fun keyUp(event: Event) {
        event as KeyboardEvent
        if (event.keyCode in (0..256)) {
            justRelease[event.keyCode] = true
            pressed[event.keyCode] = false
            event.preventDefault()
        }
    }

    override fun isKeyJustPressed(key: Key): Boolean = justPressed[key.keyCode]

    override fun isKeyPressed(key: Key): Boolean = pressed[key.keyCode]

    override fun isTouched(signal: TouchSignal): Vector2? = touchManager.isTouched(signal)

    override fun isJustTouched(signal: TouchSignal): Vector2? = touchManager.isJustTouched(signal)

    override fun record() = Unit

    override fun reset() {
        (justPressed.indices).forEach {
            justPressed[it] = false
        }
        touchManager.reset()
    }
}
