import com.github.dwursteisen.minigdx.GameConfiguration
import com.github.dwursteisen.minigdx.GameScreenConfiguration
import com.github.dwursteisen.minigdx.Window
import com.github.dwursteisen.minigdx.file.TextureImage
import java.nio.ByteBuffer

actual fun createGameConfiguration(): GameConfiguration {
    return GameConfiguration(
        "game name",
        GameScreenConfiguration.WithCurrentScreenResolution(),
        false,
        50,
        window = Window(
            width = 1024,
            height = 1024,
            name = "window name"
        )
    )
}

actual fun createTextureImage(): TextureImage {
    return TextureImage(10, 10, 0, 0, ByteBuffer.allocate(1))
}
