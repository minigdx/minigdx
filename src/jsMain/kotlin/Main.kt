import com.github.dwursteisen.minigdx.DemoPlanet
import com.github.dwursteisen.minigdx.GLConfiguration
import com.github.dwursteisen.minigdx.configuration

fun main() = configuration(GLConfiguration(canvasId = "canvas")).run { DemoPlanet() }
