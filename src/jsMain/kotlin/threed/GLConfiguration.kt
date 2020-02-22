package threed

import org.khronos.webgl.WebGLRenderingContext
import org.w3c.dom.HTMLCanvasElement
import threed.file.FileHander
import threed.input.InputHandler
import threed.input.InputManager
import threed.input.JsInputHandler
import kotlin.browser.document
import kotlin.browser.window

actual class GLConfiguration(
    val canvas: HTMLCanvasElement? = null,
    val canvasId: String? = null
)

actual class GLContext actual constructor(private val configuration: GLConfiguration) {

    private var then = 0.0
    private lateinit var game: Game
    private lateinit var inputManager: InputManager

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

    internal actual fun createFileHandler(): FileHander {
        return FileHander()
    }

    internal actual fun createInputHandler(): InputHandler {
        return JsInputHandler()
    }

    actual fun run(gameFactory: () -> Game) {
        inputManager = inp as InputManager

        this.game = gameFactory()

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
