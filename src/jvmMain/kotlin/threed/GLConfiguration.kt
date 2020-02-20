package threed

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL
import threed.file.FileHander

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

    internal actual fun createContext(): GL {
        return LwjglGL(canvas = Canvas(configuration.width, configuration.height))
    }

    internal actual fun createFileHandler(): FileHander {
        return FileHander()
    }

    /**
     * Get the time in milliseconds
     *
     * @return The system time in milliseconds
     */
    private fun getTime(): Long {
        return System.nanoTime() / 1000000
    }

    fun getDelta(): Float {
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

        glPolygonMode( GL_FRONT_AND_BACK, GL_LINE )

        while (!glfwWindowShouldClose(window)) {
            game.render(getDelta())
            glfwSwapBuffers(window) // swap the color buffers
            glfwPollEvents()
        }
        game.pause()
        game.destroy()
    }
}
