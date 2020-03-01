package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.input.LwjglInput
import org.lwjgl.glfw.GLFW.GLFW_FALSE
import org.lwjgl.glfw.GLFW.GLFW_RESIZABLE
import org.lwjgl.glfw.GLFW.GLFW_TRUE
import org.lwjgl.glfw.GLFW.GLFW_VISIBLE
import org.lwjgl.glfw.GLFW.glfwCreateWindow
import org.lwjgl.glfw.GLFW.glfwDefaultWindowHints
import org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor
import org.lwjgl.glfw.GLFW.glfwGetVideoMode
import org.lwjgl.glfw.GLFW.glfwInit
import org.lwjgl.glfw.GLFW.glfwMakeContextCurrent
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFW.glfwSetWindowPos
import org.lwjgl.glfw.GLFW.glfwShowWindow
import org.lwjgl.glfw.GLFW.glfwSwapBuffers
import org.lwjgl.glfw.GLFW.glfwSwapInterval
import org.lwjgl.glfw.GLFW.glfwWindowHint
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import org.lwjgl.system.MemoryUtil.NULL

typealias Pixels = Int

actual class GLConfiguration(
    val name: String,
    val width: Pixels,
    val height: Pixels
)

actual class GLContext actual constructor(private val configuration: GLConfiguration) {

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
        return LwjglGL(canvas = Canvas(configuration.width, configuration.height))
    }

    internal actual fun createFileHandler(): FileHandler {
        return FileHandler()
    }

    internal actual fun createInputHandler(): InputHandler {
        return LwjglInput()
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

    actual fun run(gameFactory: () -> Game) {
        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        glfwDefaultWindowHints() // optional, the current window hints are already the default

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE) // the window will be resizable

        // Create the window
        val window = glfwCreateWindow(
            configuration.width,
            configuration.height,
            configuration.name,
            NULL,
            NULL
        )
        if (window == NULL) {
            throw IllegalStateException("Failed to create the GLFW window")
        }

        // Get the resolution of the primary monitor
        val vidmode =
            glfwGetVideoMode(glfwGetPrimaryMonitor()) ?: throw IllegalStateException("No primary monitor found")
        // Center our window
        glfwSetWindowPos(
            window,
            (vidmode.width() - configuration.width) / 2,
            (vidmode.height() - configuration.height) / 2
        )

        // Make the OpenGL context current
        glfwMakeContextCurrent(window)
        // Enable v-sync
        glfwSwapInterval(1)

        org.lwjgl.opengl.GL.createCapabilities()

        // Attach input before game creation.
        val inputManager = inputs as LwjglInput
        inputManager.attachHandler(window)

        val game = gameFactory()

        // Make the window visible
        glfwShowWindow(window)
        game.create()
        game.resume()

        // Wireframe mode
        // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)

        while (!glfwWindowShouldClose(window)) {
            inputManager.record()
            game.render(getDelta())
            glfwSwapBuffers(window) // swap the color buffers
            glfwPollEvents()
            inputManager.reset()
        }
        game.pause()
        game.destroy()
    }
}
