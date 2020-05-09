package com.github.dwursteisen.minigdx

import android.graphics.Point
import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.file.PlatformFileHandler
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
        return FileHandler(handler = PlatformFileHandler(configuration.activity))
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
