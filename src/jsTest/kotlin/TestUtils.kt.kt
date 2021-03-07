import com.github.dwursteisen.minigdx.GameConfiguration
import com.github.dwursteisen.minigdx.GameScreenConfiguration

actual fun createGameConfiguration(): GameConfiguration {
    return GameConfiguration(
        "game name",
        false,
        GameScreenConfiguration.WithCurrentScreenResolution(),
        rootPath = ""
    )
}
