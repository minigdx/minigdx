package com.github.dwursteisen.minigdx.input

import com.github.dwursteisen.minigdx.logger.Logger
import com.github.dwursteisen.minigdx.math.Vector2
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
import java.nio.DoubleBuffer

class LwjglInput(val logger: Logger) : InputHandler, InputManager {

    private val touchManager = TouchManager(GLFW_KEY_LAST)

    private var window: Long = 0

    private val b1: DoubleBuffer = BufferUtils.createDoubleBuffer(1)
    private val b2: DoubleBuffer = BufferUtils.createDoubleBuffer(1)

    private fun keyDown(event: Int) {
        logger.debug("INPUT_HANDLER") { "${Thread.currentThread().name} Key pushed $event" }
        touchManager.onKeyPressed(event)
    }

    private fun keyUp(event: Int) {
        logger.debug("INPUT_HANDLER") { "Key release $event" }
        touchManager.onKeyReleased(event)
    }

    fun attachHandler(windowAddress: Long) {
        window = windowAddress
        glfwSetInputMode(windowAddress, GLFW_STICKY_KEYS, GLFW_TRUE)
        glfwSetKeyCallback(
            windowAddress,
            object : GLFWKeyCallback() {
                override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
                    if (action == GLFW_PRESS) {
                        keyDown(key)
                    } else if (action == GLFW_RELEASE) {
                        keyUp(key)
                    }
                }
            }
        )
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

    override fun reset() = touchManager.processReceivedEvent()

    override fun isKeyJustPressed(key: Key): Boolean = touchManager.isKeyJustPressed(key.keyCode)

    override fun isKeyPressed(key: Key): Boolean = touchManager.isKeyPressed(key.keyCode)

    override fun isTouched(signal: TouchSignal): Vector2? = touchManager.isTouched(signal)

    override fun isJustTouched(signal: TouchSignal): Vector2? = touchManager.isJustTouched(signal)
}
