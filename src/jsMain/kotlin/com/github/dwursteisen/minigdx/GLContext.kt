package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.file.PlatformFileHandler
import com.github.dwursteisen.minigdx.graphics.FillViewportStrategy
import com.github.dwursteisen.minigdx.graphics.ViewportStrategy
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.input.InputManager
import com.github.dwursteisen.minigdx.input.JsInputHandler
import com.github.dwursteisen.minigdx.logger.JsLogger
import com.github.dwursteisen.minigdx.logger.Logger
import kotlin.browser.window
import kotlin.math.min
import org.khronos.webgl.WebGLRenderingContextBase
import org.w3c.dom.HTMLCanvasElement

actual class GLContext actual constructor(private val configuration: GLConfiguration) {

    private var then = 0.0
    private lateinit var game: Game
    private lateinit var inputManager: InputManager
    private lateinit var canvas: HTMLCanvasElement
    private lateinit var gameContext: GameContext

    internal actual fun createContext(): GL {
        canvas =
            configuration.canvas ?: throw RuntimeException("<canvas> with id '${configuration.canvasId}' not found")

        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        val context = canvas.getContext("webgl2") as WebGLRenderingContextBase
        return WebGL(
            context, Screen(
                width = canvas.clientWidth,
                height = canvas.clientHeight
            )
        )
    }

    internal actual fun createFileHandler(logger: Logger): FileHandler {
        logger.info("GL_CONTEXT") { "Creating FileHandler with path root '${configuration.rootPath}'" }
        return FileHandler(PlatformFileHandler(configuration.rootPath, logger), logger = logger)
    }

    internal actual fun createInputHandler(logger: Logger): InputHandler {
        return JsInputHandler(canvas)
    }

    internal actual fun createViewportStrategy(logger: Logger): ViewportStrategy {
        return FillViewportStrategy(logger)
    }

    internal actual fun createLogger(): Logger {
        return JsLogger(configuration.gameName)
    }

    internal actual fun createOptions(): Options {
        return Options(configuration.debug)
    }

    actual fun run(gameContext: GameContext, gameFactory: (GameContext) -> Game) {
        this.gameContext = gameContext
        inputManager = gameContext.input as InputManager

        this.game = gameFactory(gameContext)

        window.requestAnimationFrame(::loading)
    }

    private fun loading(now: Double) {
        if (!gameContext.fileHandler.isFullyLoaded()) {
            configuration.loadingListener(gameContext.fileHandler.loadingProgress())
            window.requestAnimationFrame(::loading)
        } else {
            configuration.loadingListener(gameContext.fileHandler.loadingProgress())
            gameContext.viewport.update(
                gameContext.gl,
                gameContext.gl.screen.width,
                gameContext.gl.screen.height
            )

            game.create()
            game.resume()
            window.requestAnimationFrame(::render)
        }
    }

    private fun render(now: Double) {
        // The canvas has been resized
        // if (canvas.clientWidth != gl.screen.width || canvas.clientHeight != gl.screen.height) {
        if (canvas.clientWidth != gameContext.gl.screen.width || canvas.clientHeight != gameContext.gl.screen.height) {
            gameContext.gl.screen.width = canvas.clientWidth
            gameContext.gl.screen.height = canvas.clientHeight
            canvas.height = canvas.clientHeight
            canvas.width = canvas.clientWidth

            gameContext.viewport.update(
                gameContext.gl,
                gameContext.gl.screen.width,
                gameContext.gl.screen.height
            )
        }
        inputManager.record()
        val nowInSeconds = now * 0.001
        val delta = nowInSeconds - then
        then = nowInSeconds
        game.render(min(1 / 60f, delta.toFloat()))
        inputManager.reset()

        window.requestAnimationFrame(::render)
    }
}
