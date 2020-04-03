import com.github.dwursteisen.minigdx.DemoAnimation
import com.github.dwursteisen.minigdx.DemoAnimation2
import com.github.dwursteisen.minigdx.DemoPlanet
import com.github.dwursteisen.minigdx.GLConfiguration
import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.configuration
import kotlin.browser.window
import org.w3c.dom.url.URLSearchParams

@ExperimentalStdlibApi
private val factory: Map<String, () -> Game> = mapOf(
    "animation" to { DemoAnimation() },
    "animation2" to { DemoAnimation2() }
)
@ExperimentalStdlibApi
fun main() = configuration(GLConfiguration(canvasId = "canvas")).run {
    val urlParams = URLSearchParams(window.location.search)
    val default: () -> Game = { DemoPlanet() }
    val demo = urlParams.get("demo")
    val invoke = factory[demo] ?: default
    invoke()
}
