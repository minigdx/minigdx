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
import org.khronos.webgl.WebGLRenderingContextBase
import org.w3c.dom.HTMLCanvasElement

actual class GLContext actual constructor(private val configuration: GLConfiguration) {

    private var then = 0.0
    private lateinit var game: Game
    private lateinit var inputManager: InputManager
    private lateinit var canvas: HTMLCanvasElement
    private lateinit var gl: GL

    internal actual fun createContext(): GL {
        canvas =
            configuration.canvas ?: throw RuntimeException("<canvas> with id '${configuration.canvasId}' not found")

        val context = canvas.getContext("webgl2") as WebGLRenderingContextBase
        gl = WebGL(
            context, Screen(
                width = canvas.clientWidth,
                height = canvas.clientHeight
            )
        )
        return gl
    }

    internal actual fun createFileHandler(): FileHandler {
        return FileHandler(PlatformFileHandler())
    }

    internal actual fun createInputHandler(): InputHandler {
        return JsInputHandler(canvas)
    }

    internal actual fun createViewportStrategy(): ViewportStrategy {
        return FillViewportStrategy()
    }

    internal actual fun createLogger(): Logger {
        return JsLogger()
    }

    actual fun run(gameFactory: (GL) -> Game) {
        inputManager = inputs as InputManager

        this.game = gameFactory(gl)

        window.requestAnimationFrame(::loading)
    }

    private fun loading(now: Double) {
        if (!fileHandler.isFullyLoaded()) {
            window.requestAnimationFrame(::loading)
        } else {
            viewport.update(gl, game.worldResolution, gl.screen.width, gl.screen.height)

            game.create()
            game.resume()
            window.requestAnimationFrame(::render)
        }
    }

    private fun render(now: Double) {
        // The canvas has been resized
        // if (canvas.clientWidth != gl.screen.width || canvas.clientHeight != gl.screen.height) {
        if (canvas.clientWidth != game.worldResolution.width || canvas.clientHeight != game.worldResolution.height) {
            gl.screen.width = canvas.clientWidth
            gl.screen.height = canvas.clientHeight
            canvas.height = canvas.clientHeight
            canvas.width = canvas.clientWidth

            viewport.update(gl, game.worldResolution, gl.screen.width, gl.screen.height)
        }
        inputManager.record()
        val nowInSeconds = now * 0.001
        val delta = nowInSeconds - then
        then = nowInSeconds
        game.render(delta.toFloat())
        inputManager.reset()

        window.requestAnimationFrame(::render)
    }
}
