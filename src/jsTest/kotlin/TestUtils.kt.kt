import com.github.dwursteisen.minigdx.GameConfiguration
import com.github.dwursteisen.minigdx.GameScreenConfiguration
import com.github.dwursteisen.minigdx.file.TextureImage
import org.khronos.webgl.TexImageSource

actual fun createGameConfiguration(): GameConfiguration {
    return GameConfiguration(
        "game name",
        false,
        50,
        GameScreenConfiguration.WithCurrentScreenResolution(),
        rootPath = ""
    )
}

class MockTexImageSource : TexImageSource

actual fun createTextureImage(): TextureImage {
    return TextureImage(MockTexImageSource(), 10, 10)
}
