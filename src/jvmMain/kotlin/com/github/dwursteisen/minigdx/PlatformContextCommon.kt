package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.file.FileHandlerCommon
import com.github.dwursteisen.minigdx.file.PlatformFileHandler
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.game.GameWrapper
import com.github.dwursteisen.minigdx.graphics.FillViewportStrategy
import com.github.dwursteisen.minigdx.graphics.ViewportStrategy
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.input.LwjglInput
import com.github.dwursteisen.minigdx.logger.JavaLoggingLogger
import com.github.dwursteisen.minigdx.logger.Logger
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK
import org.lwjgl.opengl.GL11.GL_LINE
import org.lwjgl.system.MemoryUtil
import kotlin.math.min

actual open class PlatformContextCommon actual constructor(actual override val configuration: GameConfiguration) :
    PlatformContext {

    private fun isMacOs(): Boolean {
        val osName = System.getProperty("os.name")?.toLowerCase()
        val index = osName?.indexOf("mac") ?: -1
        return index >= 0
    }

    actual override fun createGL(): GL {
        return LwjglGL()
    }

    actual override fun createFileHandler(logger: Logger, gameContext: GameContext): FileHandler {
        return FileHandlerCommon(gameContext, platformFileHandler = PlatformFileHandler(logger), logger = logger)
    }

    actual override fun createInputHandler(logger: Logger, gameContext: GameContext): InputHandler {
        return LwjglInput(logger, gameContext)
    }

    actual override fun createViewportStrategy(logger: Logger): ViewportStrategy {
        return FillViewportStrategy(logger)
    }

    actual override fun createLogger(): Logger {
        return JavaLoggingLogger(configuration.window.name)
    }

    actual override fun createOptions(): Options {
        return Options(configuration.debug)
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
        return min(delta / 1000f, 1 / 60f)
    }

    private var lastFrame: Long = getTime()

    actual override fun start(gameFactory: (GameContext) -> Game) {
        if (isMacOs()) {
            System.err.println(
                """WARNING : You're runing a game on Mac OS. If the game crash at start, add -XstartOnFirstThread as JVM arguments to your program."""".trimMargin()
            )
        }

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
            configuration.window.width,
            configuration.window.height,
            configuration.window.name,
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
            (vidmode.width() - configuration.window.width) / 2,
            (vidmode.height() - configuration.window.height) / 2
        )

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(window)
        // Enable v-sync
        GLFW.glfwSwapInterval(1)

        org.lwjgl.opengl.GL.createCapabilities()

        // Make the window visible
        GLFW.glfwShowWindow(window)

        // Get the size of the device window
        val tmpWidth = MemoryUtil.memAllocInt(1)
        val tmpHeight = MemoryUtil.memAllocInt(1)
        GLFW.glfwGetWindowSize(window, tmpWidth, tmpHeight)

        // Compute the Game Resolution regarding the configuration
        val gameResolution = configuration.gameScreenConfiguration.screen(
            tmpWidth.get(0),
            tmpHeight.get(0)
        )
        val gameContext = GameContext(this, gameResolution)
        gameContext.logPlatform()

        // Update the device screen regarding the actual size of the window.
        gameContext.deviceScreen.width = tmpWidth.get(0)
        gameContext.deviceScreen.height = tmpHeight.get(0)

        // Update the framebuffer screen with the actual size of the framebuffer
        GLFW.glfwGetFramebufferSize(window, tmpWidth, tmpHeight)
        gameContext.frameBufferScreen.width = tmpWidth.get(0)
        gameContext.frameBufferScreen.height = tmpHeight.get(0)

        GLFW.glfwSetWindowSizeCallback(window) { _, width, height ->
            gameContext.deviceScreen.width = width
            gameContext.deviceScreen.height = height
        }

        GLFW.glfwSetFramebufferSizeCallback(window) { _, width, height ->
            gameContext.frameBufferScreen.width = width
            gameContext.frameBufferScreen.height = height
            gameContext.viewport.update(
                gameContext.gl,
                gameContext.frameBufferScreen.width,
                gameContext.frameBufferScreen.height,
                gameContext.gameScreen.width,
                gameContext.gameScreen.height
            )
        }

        GLFW.glfwSetWindowPosCallback(window) { _, _, _ ->
            // The window moved. The viewport needs to be refreshed in this situation too.
            gameContext.viewport.update(
                gameContext.gl,
                gameContext.frameBufferScreen.width,
                gameContext.frameBufferScreen.height,
                gameContext.gameScreen.width,
                gameContext.gameScreen.height
            )
        }

        // Attach input before game creation.
        val inputManager = gameContext.input as LwjglInput
        inputManager.attachHandler(window)

        val game = GameWrapper(gameContext, gameFactory(gameContext))

        game.create()
        game.resume()

        // Wireframe mode
        if (configuration.wireframe) {
            GL11.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
        }
        gameContext.viewport.update(
            gameContext.gl,
            gameContext.frameBufferScreen.width,
            gameContext.frameBufferScreen.height,
            gameContext.gameScreen.width,
            gameContext.gameScreen.height
        )
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
