import com.github.dwursteisen.minigdx.GameConfiguration
import com.github.dwursteisen.minigdx.GameScreenConfiguration
import com.github.dwursteisen.minigdx.Window

actual fun createGameConfiguration(): GameConfiguration {
    return GameConfiguration(
        "game name",
        GameScreenConfiguration.WithCurrentScreenResolution(),
        false,
        window = Window(
            width = 1024,
            height = 1024,
            name = "window name"
        )
    )
}
