package com.github.dwursteisen.minigdx

import android.graphics.Point
import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.file.FileHandlerCommon
import com.github.dwursteisen.minigdx.file.PlatformFileHandler
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.graphics.FillViewportStrategy
import com.github.dwursteisen.minigdx.graphics.ViewportStrategy
import com.github.dwursteisen.minigdx.input.AndroidInputHandler
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.internal.MiniGdxSurfaceView
import com.github.dwursteisen.minigdx.logger.AndroidLogger
import com.github.dwursteisen.minigdx.logger.Logger

actual open class PlatformContextCommon actual constructor(
    private val configuration: GameConfiguration
) : PlatformContext {

    actual override fun createGL(): GL {
        val display = configuration.activity!!.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return AndroidGL(ScreenConfiguration(size.x, size.y))
    }

    actual override fun createFileHandler(logger: Logger): FileHandler {
        return FileHandlerCommon(platformFileHandler = PlatformFileHandler(configuration.activity!!, logger), logger = logger)
    }

    actual override fun createViewportStrategy(logger: Logger): ViewportStrategy {
        return FillViewportStrategy(logger)
    }

    actual override fun createInputHandler(logger: Logger): InputHandler {
        return AndroidInputHandler()
    }

    actual override fun createLogger(): Logger {
        return AndroidLogger(configuration.gameName)
    }

    actual override fun createOptions(): Options {
        return Options(
            debug = configuration.debug
        )
    }

    @ExperimentalStdlibApi
    actual override fun start(gameContext: GameContext, gameFactory: (GameContext) -> Game) {
        val surfaceView = MiniGdxSurfaceView(gameContext, configuration.activity!!)
        configuration.activity.setContentView(surfaceView)
    }
}
