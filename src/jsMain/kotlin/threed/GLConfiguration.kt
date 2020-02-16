package threed

import org.khronos.webgl.WebGLRenderingContext
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.browser.window

actual class GLConfiguration actual constructor() {

    var canvas: HTMLCanvasElement? = null
    var canvasId: String? = null

    private var then = 0.0
    private lateinit var game: Game

    actual fun createContext(): GL {
        canvas = canvas ?: canvasId?.let { document.getElementById(it) as? HTMLCanvasElement }
                ?: throw RuntimeException("<canvas> with id '${canvasId}' not found")

        val context = canvas?.getContext("webgl") as WebGLRenderingContext
        return WebGL(context, Canvas(
            width = canvas!!.clientWidth,
            height = canvas!!.clientHeight
        ))
    }

    actual fun mainLoop(game: Game) {
        this.game = game

        game.create()
        game.resume()
        window.requestAnimationFrame(::render)
    }

    private fun render(now: Double) {
        val nowInSeconds = now * 0.001
        val delta = nowInSeconds - then
        then = nowInSeconds
        game.render(delta.toFloat())
        window.requestAnimationFrame(::render)
    }
}
