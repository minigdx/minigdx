package com.github.dwursteisen.minigdx

import android.graphics.Point
import com.github.dwursteisen.minigdx.entity.animations.AnimatedModel
import com.github.dwursteisen.minigdx.entity.delegate.Model
import com.github.dwursteisen.minigdx.entity.primitives.Texture
import com.github.dwursteisen.minigdx.entity.text.AngelCode
import com.github.dwursteisen.minigdx.entity.text.Text
import com.github.dwursteisen.minigdx.file.AngelCodeLoader
import com.github.dwursteisen.minigdx.file.AnimatedModelLoader
import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.file.ModelLoader
import com.github.dwursteisen.minigdx.file.PlatformFileHandler
import com.github.dwursteisen.minigdx.file.TextLoader
import com.github.dwursteisen.minigdx.file.TextureImage
import com.github.dwursteisen.minigdx.file.TextureImageLoader
import com.github.dwursteisen.minigdx.file.TextureLoader
import com.github.dwursteisen.minigdx.graphics.FillViewportStrategy
import com.github.dwursteisen.minigdx.graphics.ViewportStrategy
import com.github.dwursteisen.minigdx.input.AndroidInputHandler
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.internal.MiniGdxSurfaceView
import com.github.dwursteisen.minigdx.logger.AndroidLogger
import com.github.dwursteisen.minigdx.logger.Logger

@ExperimentalStdlibApi
actual class GLContext actual constructor(private val configuration: GLConfiguration) {
    internal actual fun createContext(): GL {
        val display = configuration.activity.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return AndroidGL(Screen(size.x, size.y))
    }

    @ExperimentalStdlibApi
    internal actual fun createFileHandler(): FileHandler {
        return FileHandler(
            handler = PlatformFileHandler(configuration.activity),
            loaders = mapOf(
                AnimatedModel::class to AnimatedModelLoader(),
                Model::class to ModelLoader(),
                TextureImage::class to TextureImageLoader(),
                Texture::class to TextureLoader(),
                AngelCode::class to AngelCodeLoader(),
                Text::class to TextLoader()
            )
        )
    }

    internal actual fun createViewportStrategy(): ViewportStrategy {
        return FillViewportStrategy()
    }

    internal actual fun createInputHandler(): InputHandler {
        return AndroidInputHandler()
    }

    internal actual fun createLogger(): Logger {
        return AndroidLogger()
    }

    actual fun run(gameFactory: () -> Game) {
        val surfaceView = MiniGdxSurfaceView(configuration.activity)
        configuration.activity.setContentView(surfaceView)
    }
}
