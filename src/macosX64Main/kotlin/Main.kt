import com.github.dwursteisen.minigdx.DemoGame
import com.github.dwursteisen.minigdx.GLConfiguration
import com.github.dwursteisen.minigdx.configuration

fun main() = configuration(
    GLConfiguration(
        title = "Kotlin/Native Metal",
        width = 800,
        height = 800
    )
).run { DemoGame() }
