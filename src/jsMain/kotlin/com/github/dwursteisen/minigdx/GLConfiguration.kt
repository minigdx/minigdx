package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.graphics.FillViewportStrategy
import com.github.dwursteisen.minigdx.graphics.ViewportStrategy
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.input.InputManager
import com.github.dwursteisen.minigdx.input.JsInputHandler
import kotlin.browser.document
import kotlin.browser.window
import org.khronos.webgl.WebGLRenderingContext
import org.w3c.dom.HTMLCanvasElement

actual class GLConfiguration(
    val canvas: HTMLCanvasElement? = null,
    val canvasId: String? = null
)

actual class GLContext actual constructor(private val configuration: GLConfiguration) {

    private var then = 0.0
    private lateinit var game: Game
    private lateinit var inputManager: InputManager
    private lateinit var canvas: HTMLCanvasElement

    internal actual fun createContext(): GL {
        canvas =
            configuration.canvas ?: configuration.canvasId?.let { document.getElementById(it) as? HTMLCanvasElement }
            ?: throw RuntimeException("<canvas> with id '${configuration.canvasId}' not found")

        val context = canvas.getContext("webgl") as WebGLRenderingContext
        return WebGL(
            context, Screen(
                width = canvas.clientWidth,
                height = canvas.clientHeight
            )
        )
    }

    internal actual fun createFileHandler(): FileHandler {
        return FileHandler()
    }

    internal actual fun createInputHandler(): InputHandler {
        return JsInputHandler(canvas)
    }

    internal actual fun createViewportStrategy(): ViewportStrategy {
        return FillViewportStrategy()
    }

    actual fun run(gameFactory: () -> Game) {
        inputManager = inputs as InputManager

        this.game = gameFactory()

        viewport.update(game.worldSize, gl.screen.width, gl.screen.height)

        game.create()
        game.resume()
        window.requestAnimationFrame(::render)
    }

    private fun render(now: Double) {
        inputManager.record()
        val nowInSeconds = now * 0.001
        val delta = nowInSeconds - then
        then = nowInSeconds
        game.render(delta.toFloat())
        inputManager.reset()
        window.requestAnimationFrame(::render)
    }
}
