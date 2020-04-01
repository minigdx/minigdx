import com.github.dwursteisen.minigdx.DemoAnimation
import com.github.dwursteisen.minigdx.DemoPlanet
import com.github.dwursteisen.minigdx.GLConfiguration
import com.github.dwursteisen.minigdx.configuration
import kotlin.browser.window
import org.w3c.dom.url.URLSearchParams

@ExperimentalStdlibApi
fun main() = configuration(GLConfiguration(canvasId = "canvas")).run {
    val urlParams = URLSearchParams(window.location.search)
    if (urlParams.get("demo") == "animation") {
        DemoAnimation()
    } else {
        DemoPlanet()
    }
}
