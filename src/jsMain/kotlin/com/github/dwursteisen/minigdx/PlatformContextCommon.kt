package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.audio.AudioContext
import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.file.FileHandlerCommon
import com.github.dwursteisen.minigdx.file.PlatformFileHandler
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.game.GameWrapper
import com.github.dwursteisen.minigdx.graphics.FillViewportStrategy
import com.github.dwursteisen.minigdx.graphics.ViewportStrategy
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.input.InputManager
import com.github.dwursteisen.minigdx.input.JsInputHandler
import com.github.dwursteisen.minigdx.logger.JsLogger
import com.github.dwursteisen.minigdx.logger.Logger
import com.github.dwursteisen.minigdx.logger.profile
import kotlinx.browser.window
import org.khronos.webgl.WebGLRenderingContextBase
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.get
import kotlin.math.min

actual open class PlatformContextCommon actual constructor(
    actual override val configuration: GameConfiguration
) : PlatformContext {

    private var then = 0.0
    private lateinit var gameWrapper: GameWrapper
    private lateinit var inputManager: InputManager
    private lateinit var canvas: HTMLCanvasElement
    private lateinit var gameContext: GameContext

    actual override fun createGL(): GL {
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        val context = canvas.getContext("webgl2") as? WebGLRenderingContextBase // Most browsers
            ?: oldWebGL() // Safari
        return WebGL(context)
    }

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    private fun oldWebGL(): WebGLRenderingContextBase {
        console.warn("WebGL 2 not detected! Falling back to WebGL 1.")
        console.warn(
            "MiniGDX might not work as expected as the game is run from a browser" +
                "that might not support all features required (like Web GL 2, Audio API, ...)"
        )
        console.warn(
            "Please warn your user to update their browser or switch to anoter browser " +
                "like Firefox, Chrome or Opera."
        )
        return canvas.getContext("webgl") as WebGLRenderingContextBase
    }

    actual override fun createFileHandler(logger: Logger): FileHandler {
        logger.info("GL_CONTEXT") { "Creating FileHandler with path root '${configuration.rootPath}'" }

        // Audio fix for Safara
        if (window.get("AudioContext") == undefined) {
            js("window.AudioContext = webkitAudioContext;")
        }
        return FileHandlerCommon(
            PlatformFileHandler(
                configuration.rootPath,
                AudioContext(),
                logger
            ),
            logger = logger
        )
    }

    actual override fun createInputHandler(logger: Logger, gameContext: GameContext): InputHandler {
        return JsInputHandler(canvas, gameContext)
    }

    actual override fun createViewportStrategy(logger: Logger): ViewportStrategy {
        return FillViewportStrategy(logger)
    }

    actual override fun createLogger(): Logger {
        return JsLogger(configuration.gameName)
    }

    actual override fun createOptions(): Options {
        return Options(configuration.debug)
    }

    actual override fun start(gameFactory: (GameContext) -> Game) {
        canvas = configuration.canvas
            ?: throw RuntimeException("<canvas> with id '${configuration.canvasId}' not found")

        val resolution = configuration.gameScreenConfiguration.screen(
            canvas.clientWidth,
            canvas.clientHeight
        )
        this.gameContext = GameContext(this, resolution)
        this.gameContext.logPlatform()
        this.gameContext.deviceScreen.width = canvas.clientWidth
        this.gameContext.deviceScreen.height = canvas.clientHeight
        this.gameContext.frameBufferScreen.width = canvas.clientWidth
        this.gameContext.frameBufferScreen.height = canvas.clientHeight

        inputManager = gameContext.input as InputManager

        val game = gameFactory(gameContext)
        this.gameWrapper = GameWrapper(gameContext, game)
        window.requestAnimationFrame(::loading)
    }

    private fun loading(@Suppress("UNUSED_PARAMETER") now: Double) {
        if (!gameContext.fileHandler.isFullyLoaded()) {
            configuration.loadingListener(gameContext.fileHandler.loadingProgress())
            window.requestAnimationFrame(::loading)
        } else {
            configuration.loadingListener(gameContext.fileHandler.loadingProgress())
            gameContext.viewport.update(
                gameContext.gl,
                gameContext.frameBufferScreen.width,
                gameContext.frameBufferScreen.height,
                gameContext.gameScreen.width,
                gameContext.gameScreen.height
            )

            gameWrapper.create()
            gameWrapper.resume()
            window.requestAnimationFrame(::render)
        }
    }

    private fun render(now: Double) {
        // The canvas has been resized
        if (canvas.clientWidth != gameContext.deviceScreen.width ||
            canvas.clientHeight != gameContext.deviceScreen.height
        ) {
            gameContext.deviceScreen.width = canvas.clientWidth
            gameContext.deviceScreen.height = canvas.clientHeight
            gameContext.frameBufferScreen.width = canvas.clientWidth
            gameContext.frameBufferScreen.height = canvas.clientHeight
            canvas.width = canvas.clientWidth
            canvas.height = canvas.clientHeight

            gameContext.viewport.update(
                gameContext.gl,
                gameContext.frameBufferScreen.width,
                gameContext.frameBufferScreen.height,
                gameContext.gameScreen.width,
                gameContext.gameScreen.height
            )
        }
        val nowInSeconds = now * 0.001
        val delta = nowInSeconds - then
        then = nowInSeconds
        val deltaCapped = min(1 / 60f, delta.toFloat())
        gameContext.assetsManager.update(deltaCapped)
        inputManager.record()
        profile("render") {
            gameWrapper.render(deltaCapped)
        }
        inputManager.reset()

        window.requestAnimationFrame(::render)
    }
}
