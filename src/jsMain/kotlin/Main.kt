import com.github.dwursteisen.minigdx.GLConfiguration
import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.configuration
import com.github.dwursteisen.minigdx.demo.DemoAnimation
import com.github.dwursteisen.minigdx.demo.DemoAnimation2
import com.github.dwursteisen.minigdx.demo.DemoApiV2
import com.github.dwursteisen.minigdx.demo.DemoCamera
import com.github.dwursteisen.minigdx.demo.DemoGame
import com.github.dwursteisen.minigdx.demo.DemoKey
import com.github.dwursteisen.minigdx.demo.DemoLight
import com.github.dwursteisen.minigdx.demo.DemoMovable
import com.github.dwursteisen.minigdx.demo.DemoSuzanne
import com.github.dwursteisen.minigdx.demo.DemoSuzanne2
import com.github.dwursteisen.minigdx.demo.DemoTexture
import com.github.dwursteisen.minigdx.demo.DemoTriangle
import kotlin.browser.document
import kotlin.browser.window
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.url.URLSearchParams

@ExperimentalStdlibApi
private val factory: Map<String, () -> Game> = mapOf(
    "animation" to { DemoAnimation() },
    "animation2" to { DemoAnimation2() },
    "suzanne" to { DemoSuzanne() },
    "suzanne2" to { DemoSuzanne2() },
    "2d" to { DemoTexture() },
    "key" to { DemoKey() },
    "game" to { DemoGame() },
    "camera" to { DemoCamera() },
    "move" to { DemoMovable() },
    "triange" to { DemoTriangle() },
    "light" to { DemoLight() },
    "v2" to { DemoApiV2() }
)

@ExperimentalStdlibApi
fun onlyInBrowser(block: () -> Unit) {
    try {
        js("if(typeof(window) !== 'undefined') { block() }")
    } finally {
    }
}

@ExperimentalStdlibApi
fun main() = onlyInBrowser {
    val canvas = document.getElementById("canvas") as? HTMLCanvasElement ?: return@onlyInBrowser
    configuration(GLConfiguration(canvas = canvas)).run {
        val urlParams = URLSearchParams(window.location.search)
        val default: () -> Game = { DemoApiV2() }
        val demo = urlParams.get("demo")
        val invoke = factory[demo] ?: default
        invoke()
    }
}
