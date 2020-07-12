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

    private lateinit var gl: GL

    internal actual fun createContext(): GL {
        val display = configuration.activity.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        gl = AndroidGL(Screen(size.x, size.y))
        return gl
    }

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

    @ExperimentalStdlibApi
    actual fun run(gameContext: GameContext, gameFactory: (GameContext) -> Game) {
        val surfaceView = MiniGdxSurfaceView(gameContext, configuration.activity)
        configuration.activity.setContentView(surfaceView)
    }
}
