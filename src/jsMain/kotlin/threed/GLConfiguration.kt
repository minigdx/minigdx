package threed

import org.khronos.webgl.WebGLRenderingContext
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.browser.window

actual class GLConfiguration(
    val canvas: HTMLCanvasElement? = null,
    val canvasId: String? = null
)

actual class GLContext actual constructor(private val configuration: GLConfiguration) {

    private var then = 0.0
    private lateinit var game: Game

    internal actual fun createContext(): GL {
        val canvas =
            configuration.canvas ?: configuration.canvasId?.let { document.getElementById(it) as? HTMLCanvasElement }
            ?: throw RuntimeException("<canvas> with id '${configuration.canvasId}' not found")

        val context = canvas.getContext("webgl") as WebGLRenderingContext
        return WebGL(
            context, Canvas(
                width = canvas.clientWidth,
                height = canvas.clientHeight
            )
        )
    }

    actual fun run(gameFactory: () -> Game) {
        this.game = gameFactory()

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
