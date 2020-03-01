package com.github.dwursteisen.minigdx.input

import com.github.dwursteisen.minigdx.math.Vector2
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE
import org.lwjgl.glfw.GLFW.GLFW_STICKY_KEYS
import org.lwjgl.glfw.GLFW.GLFW_TRUE
import org.lwjgl.glfw.GLFW.glfwSetInputMode
import org.lwjgl.glfw.GLFW.glfwSetKeyCallback
import org.lwjgl.glfw.GLFWKeyCallback

class LwjglInput : InputHandler, InputManager {

    private val keys: Array<Boolean> = Array(256 + 1) { false }
    private val pressed: Array<Boolean> = Array(256 + 1) { false }

    private fun keyDown(event: Int) {
        if (event in (0..256)) {
            keys[event] = true
            pressed[event] = true
        }
    }

    private fun keyUp(event: Int) {
        if (event in (0..256)) {
            keys[event] = false
        }
    }

    fun attachHandler(windowAddress: Long) {
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

    override fun record() = Unit

    override fun reset() {
        for (i in pressed.indices) {
            pressed[i] = false
        }
    }

    override fun isKey(key: Key): Boolean = keys[key.keyCode]

    override fun isKeyPressed(key: Key): Boolean = pressed[key.keyCode]

    override fun isTouched(signal: TouchSignal): Vector2? = null

    override fun isJustTouched(signal: TouchSignal): Vector2? = null
}
