import com.github.dwursteisen.minigdx.GLConfiguration
import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.configuration
import com.github.dwursteisen.minigdx.demo.DemoAnimation
import com.github.dwursteisen.minigdx.demo.DemoAnimation2
import com.github.dwursteisen.minigdx.demo.DemoCamera
import com.github.dwursteisen.minigdx.demo.DemoGame
import com.github.dwursteisen.minigdx.demo.DemoKey
import com.github.dwursteisen.minigdx.demo.DemoMovable
import com.github.dwursteisen.minigdx.demo.DemoPlanet
import com.github.dwursteisen.minigdx.demo.DemoSuzanne
import com.github.dwursteisen.minigdx.demo.DemoTexture
import kotlin.browser.window
import org.w3c.dom.url.URLSearchParams

@ExperimentalStdlibApi
private val factory: Map<String, () -> Game> = mapOf(
    "animation" to { DemoAnimation() },
    "animation2" to { DemoAnimation2() },
    "suzanne" to { DemoSuzanne() },
    "2d" to { DemoTexture() },
    "key" to { DemoKey() },
    "game" to { DemoGame() },
    "camera" to { DemoCamera() },
    "move" to { DemoMovable() }
)
@ExperimentalStdlibApi
fun main() = configuration(GLConfiguration(canvasId = "canvas")).run {
    val urlParams = URLSearchParams(window.location.search)
    val default: () -> Game = { DemoPlanet() }
    val demo = urlParams.get("demo")
    val invoke = factory[demo] ?: default
    invoke()
}
