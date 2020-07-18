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

actual class GLContext actual constructor(private val configuration: GLConfiguration) {

    internal actual fun createContext(): GL {
        val display = configuration.activity.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return AndroidGL(Screen(size.x, size.y))
    }

    internal actual fun createFileHandler(logger: Logger): FileHandler {
        return FileHandler(platformFileHandler = PlatformFileHandler(configuration.activity, logger), logger = logger)
    }

    internal actual fun createViewportStrategy(logger: Logger): ViewportStrategy {
        return FillViewportStrategy(logger)
    }

    internal actual fun createInputHandler(logger: Logger): InputHandler {
        return AndroidInputHandler()
    }

    internal actual fun createLogger(): Logger {
        return AndroidLogger(configuration.gameName)
    }

    @ExperimentalStdlibApi
    actual fun run(gameContext: GameContext, gameFactory: (GameContext) -> Game) {
        val surfaceView = MiniGdxSurfaceView(gameContext, configuration.activity)
        configuration.activity.setContentView(surfaceView)
    }
}
