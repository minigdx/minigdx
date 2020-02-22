package threed

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryUtil.NULL
import threed.file.FileHander
import threed.input.InputHandler
import threed.input.InputManager
import threed.input.Key
import threed.input.TouchSignal
import threed.math.Vector2
import java.lang.management.ManagementFactory

typealias Pixels = Int

actual class GLConfiguration(
    val name: String,
    val width: Pixels,
    val height: Pixels
)

actual class GLContext actual constructor(private val configuration: GLConfiguration) {

    private enum class Classifier(val exts: String) {
        WINDOWS("windows"), LINUX("linux"), MACOS("macos")
    }

    private fun isMacOs(): Boolean {
        val osName = System.getProperty("os.name").toLowerCase()
        return osName.indexOf("mac") >= 0
    }
    internal actual fun createContext(): GL {
        if(isMacOs()) {
            System.err.println("""WARNING : You're runing a game on Mac OS.
                | If the game crash at start, add -XstartOnFirstThread as JVM arguments to your program."""".trimMargin())
        }
        return LwjglGL(canvas = Canvas(configuration.width, configuration.height))
    }

    internal actual fun createFileHandler(): FileHander {
        return FileHander()
    }


    internal actual fun createInputHandler(): InputHandler {
        return object : InputHandler, InputManager {
            override fun record()  = Unit

            override fun reset() = Unit

            override fun isKey(key: Key): Boolean = false

            override fun isKeyPressed(key: Key): Boolean = false

            override fun isTouched(signal: TouchSignal): Vector2? = null

            override fun isJustTouched(signal: TouchSignal): Vector2?  = null

        }
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

        val game = gameFactory()

        // Make the window visible
        glfwShowWindow(window)
        game.create()
        game.resume()

        // Wireframe mode
        // glPolygonMode( GL_FRONT_AND_BACK, GL_LINE )

        val inputManager = inp as InputManager
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
