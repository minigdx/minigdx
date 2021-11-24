package com.github.dwursteisen.minigdx.input

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.logger.Logger
import com.github.dwursteisen.minigdx.math.Vector2
import com.github.dwursteisen.minigdx.utils.convert
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
import org.lwjgl.glfw.GLFW.glfwSetCursorEnterCallback
import org.lwjgl.glfw.GLFW.glfwSetInputMode
import org.lwjgl.glfw.GLFW.glfwSetKeyCallback
import org.lwjgl.glfw.GLFWCursorEnterCallback
import org.lwjgl.glfw.GLFWKeyCallback
import java.nio.DoubleBuffer

class LwjglInput(private val logger: Logger, private val gameContext: GameContext) : InputHandler, InputManager {

    private val touchManager = TouchManager(UNKNOWN_KEY)

    private var window: Long = 0

    private val b1: DoubleBuffer = BufferUtils.createDoubleBuffer(1)
    private val b2: DoubleBuffer = BufferUtils.createDoubleBuffer(1)

    private var mousePosition: Vector2 = Vector2(0f, 0f)
    private var isMouseInsideGameScreen: Boolean = false
    private var isMouseInsideWindow: Boolean = false

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
        glfwSetCursorEnterCallback(
            windowAddress,
            object : GLFWCursorEnterCallback() {
                override fun invoke(window: Long, entered: Boolean) {
                    isMouseInsideWindow = entered
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
                    val gamePosition = gameContext.convert(b1[0].toFloat(), b2[0].toFloat())
                    gamePosition?.let { (x, y) ->
                        touchManager.onTouchMove(touchSignal, x, y)
                    }
                } else {
                    val gamePosition = gameContext.convert(b1[0].toFloat(), b2[0].toFloat())
                    gamePosition?.let { (x, y) ->
                        touchManager.onTouchDown(touchSignal, x, y)
                    }
                }
            } else if (glfwGetMouseButton(window, glfwMouseButton) == GLFW_RELEASE) {
                touchManager.onTouchUp(touchSignal)
            }
        }
        // Update mouse position
        // https://www.glfw.org/docs/3.3/input_guide.html#cursor_pos
        if (isMouseInsideWindow) {
            glfwGetCursorPos(window, b1, b2)
            val gamePosition = gameContext.convert(b1[0].toFloat(), b2[0].toFloat())
            if (gamePosition == null) {
                // the mouse is in the window but NOT in the game screen
                isMouseInsideGameScreen = false
            } else {
                isMouseInsideGameScreen = true
                mousePosition.x = gamePosition.first
                mousePosition.y = gamePosition.second
            }
        } else {
            isMouseInsideGameScreen = false
        }

        // Update touch status
        touchStatus(GLFW_MOUSE_BUTTON_1, TouchSignal.TOUCH1)
        touchStatus(GLFW_MOUSE_BUTTON_2, TouchSignal.TOUCH2)
        touchStatus(GLFW_MOUSE_BUTTON_3, TouchSignal.TOUCH3)
    }

    override fun reset() = touchManager.processReceivedEvent()

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
        return if (isMouseInsideGameScreen) {
            mousePosition
        } else {
            null
        }
    }
}
