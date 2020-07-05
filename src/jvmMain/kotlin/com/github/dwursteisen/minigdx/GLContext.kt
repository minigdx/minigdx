package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.file.PlatformFileHandler
import com.github.dwursteisen.minigdx.graphics.FillViewportStrategy
import com.github.dwursteisen.minigdx.graphics.ViewportStrategy
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.input.LwjglInput
import com.github.dwursteisen.minigdx.logger.JavaLoggingLogger
import com.github.dwursteisen.minigdx.logger.Logger
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback
import org.lwjgl.system.MemoryUtil

actual class GLContext actual constructor(private val configuration: GLConfiguration) {

    private lateinit var gl: GL

    private fun isMacOs(): Boolean {
        val osName = System.getProperty("os.name").toLowerCase()
        return osName.indexOf("mac") >= 0
    }

    internal actual fun createContext(): GL {
        if (isMacOs()) {
            System.err.println(
                """WARNING : You're runing a game on Mac OS. If the game crash at start, add -XstartOnFirstThread as JVM arguments to your program."""".trimMargin()
            )
        }
        gl = LwjglGL(
            screen = Screen(
                configuration.width,
                configuration.height
            )
        )
        return gl
    }

    internal actual fun createFileHandler(): FileHandler {
        return FileHandler(handler = PlatformFileHandler())
    }

    internal actual fun createInputHandler(): InputHandler {
        return LwjglInput()
    }

    internal actual fun createViewportStrategy(): ViewportStrategy {
        return FillViewportStrategy()
    }

    internal actual fun createLogger(): Logger {
        return JavaLoggingLogger()
    }

    /**
     * Get the time in milliseconds
     *
     * @return The system time in milliseconds
     */
    private fun getTime(): Long {
        return System.nanoTime() / 1000000
    }

    private fun getDelta(): Float {
        val time = getTime()
        val delta = (time - lastFrame)
        lastFrame = time
        return delta / 1000f
    }

    private var lastFrame: Long = 0L

    actual fun run(gameFactory: (gl: GL) -> Game) {
        if (!GLFW.glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        GLFW.glfwDefaultWindowHints() // optional, the current window hints are already the default

        GLFW.glfwWindowHint(
            GLFW.GLFW_VISIBLE,
            GLFW.GLFW_FALSE
        ) // the window will stay hidden after creation
        GLFW.glfwWindowHint(
            GLFW.GLFW_RESIZABLE,
            GLFW.GLFW_TRUE
        ) // the window will be resizable

        // Create the window
        val window = GLFW.glfwCreateWindow(
            configuration.width,
            configuration.height,
            configuration.name,
            MemoryUtil.NULL,
            MemoryUtil.NULL
        )
        if (window == MemoryUtil.NULL) {
            throw IllegalStateException("Failed to create the GLFW window")
        }

        // Get the resolution of the primary monitor
        val vidmode =
            GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())
                ?: throw IllegalStateException("No primary monitor found")
        // Center our window
        GLFW.glfwSetWindowPos(
            window,
            (vidmode.width() - configuration.width) / 2,
            (vidmode.height() - configuration.height) / 2
        )

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(window)
        // Enable v-sync
        GLFW.glfwSwapInterval(1)

        org.lwjgl.opengl.GL.createCapabilities()

        // Attach input before game creation.
        val inputManager = inputs as LwjglInput
        inputManager.attachHandler(window)

        // Make the window visible
        GLFW.glfwShowWindow(window)
        val game = gameFactory(gl)

        game.create()
        game.resume()

        viewport.update(gl, game.worldResolution, configuration.width, configuration.height)
        glfwSetWindowSizeCallback(window) { _, w, h ->
            gl.screen.width = w
            gl.screen.height = h
            viewport.update(gl, game.worldResolution, w, h)
        }

        // Wireframe mode
        // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)

        while (!GLFW.glfwWindowShouldClose(window)) {
            inputManager.record()
            game.render(getDelta())
            GLFW.glfwSwapBuffers(window) // swap the color buffers
            GLFW.glfwPollEvents()
            inputManager.reset()
        }
        game.pause()
        game.destroy()
    }
}
