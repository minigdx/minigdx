package com.github.dwursteisen.minigdx.input

import com.github.dwursteisen.minigdx.log
import com.github.dwursteisen.minigdx.math.Vector2
import java.nio.DoubleBuffer
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.GLFW_KEY_LAST
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_3
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE
import org.lwjgl.glfw.GLFW.GLFW_STICKY_KEYS
import org.lwjgl.glfw.GLFW.GLFW_TRUE
import org.lwjgl.glfw.GLFW.glfwGetCursorPos
import org.lwjgl.glfw.GLFW.glfwGetMouseButton
import org.lwjgl.glfw.GLFW.glfwSetInputMode
import org.lwjgl.glfw.GLFW.glfwSetKeyCallback
import org.lwjgl.glfw.GLFWKeyCallback

class LwjglInput : InputHandler, InputManager {

    private val keys: Array<Long> = Array(GLFW_KEY_LAST + 1) { -1L }
    private val pressed: Array<Long> = Array(GLFW_KEY_LAST + 1) { -1L }

    private var frame = 0L

    private val touchManager = TouchManager()

    private var window: Long = 0

    private val b1: DoubleBuffer = BufferUtils.createDoubleBuffer(1)
    private val b2: DoubleBuffer = BufferUtils.createDoubleBuffer(1)

    private fun keyDown(event: Int) {
        log.debug("INPUT_HANDLER") { "${Thread.currentThread().name} Key pushed $event" }
        if (event in (0..GLFW_KEY_LAST)) {
            keys[event] = frame + 1
            pressed[event] = frame + 1
        }
    }

    private fun keyUp(event: Int) {
        log.debug("INPUT_HANDLER") { "Key release $event" }
        if (event in (0..GLFW_KEY_LAST)) {
            keys[event] = -1
        }
    }

    fun attachHandler(windowAddress: Long) {
        window = windowAddress
        glfwSetInputMode(windowAddress, GLFW_STICKY_KEYS, GLFW_TRUE)
        glfwSetKeyCallback(windowAddress, object : GLFWKeyCallback() {
            override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
                if (action == GLFW_PRESS) {
                    keyDown(key)
                } else if (action == GLFW_RELEASE) {
                    keyUp(key)
                }
            }
        })
    }

    override fun record() {
        fun touchStatus(glfwMouseButton: Int, touchSignal: TouchSignal) {
            // see https://github.com/LWJGL/lwjgl3-wiki/wiki/2.6.3-Input-handling-with-GLFW
            if (glfwGetMouseButton(window, glfwMouseButton) == GLFW_PRESS) {
                glfwGetCursorPos(window, b1, b2)
                if (touchManager.isTouched(touchSignal) != null) {
                    touchManager.onTouchMove(touchSignal, b1[0].toFloat(), b2[0].toFloat())
                } else {
                    touchManager.onTouchDown(touchSignal, b1[0].toFloat(), b2[0].toFloat())
                }
            } else if (glfwGetMouseButton(window, glfwMouseButton) == GLFW_RELEASE) {
                touchManager.onTouchUp(touchSignal)
            }
        }
        touchStatus(GLFW_MOUSE_BUTTON_1, TouchSignal.TOUCH1)
        touchStatus(GLFW_MOUSE_BUTTON_2, TouchSignal.TOUCH2)
        touchStatus(GLFW_MOUSE_BUTTON_3, TouchSignal.TOUCH3)
    }

    override fun reset() {
        frame++
    }

    override fun isKeyJustPressed(key: Key): Boolean = this.pressed[key.keyCode] == frame

    override fun isKeyPressed(key: Key): Boolean = keys[key.keyCode] != -1L

    override fun isTouched(signal: TouchSignal): Vector2? = touchManager.isTouched(signal)

    override fun isJustTouched(signal: TouchSignal): Vector2? = touchManager.isJustTouched(signal)
}
