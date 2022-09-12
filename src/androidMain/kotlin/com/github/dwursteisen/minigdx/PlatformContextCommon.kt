package com.github.dwursteisen.minigdx

import android.graphics.Point
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION
import android.media.SoundPool
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
    actual override val configuration: GameConfiguration
) : PlatformContext {

    actual override fun createGL(): GL {
        return AndroidGL()
    }

    actual override fun createFileHandler(logger: Logger, gameContext: GameContext): FileHandler {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(CONTENT_TYPE_SONIFICATION)
            .build()

        val sp = SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(audioAttributes)
            .build()

        return FileHandlerCommon(
            platformFileHandler = PlatformFileHandler(
                configuration.activity!!,
                sp,
                logger
            ),
            logger = logger,
            gameContext = gameContext
        )
    }

    actual override fun createViewportStrategy(logger: Logger): ViewportStrategy {
        return FillViewportStrategy(logger)
    }

    actual override fun createInputHandler(logger: Logger, gameContext: GameContext): InputHandler {
        return AndroidInputHandler(gameContext)
    }

    actual override fun createLogger(): Logger {
        return AndroidLogger(configuration.gameName)
    }

    actual override fun createOptions(): Options {
        return Options(
            debug = configuration.debug
        )
    }

    actual override fun start(gameFactory: (GameContext) -> Game) {
        val activity = configuration.activity!!
        val display = activity.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val resolution = configuration.gameScreenConfiguration.screen(size.x, size.y)

        val gameContext = GameContext(this, resolution)
        gameContext.deviceScreen.width = size.x
        gameContext.deviceScreen.height = size.y
        val surfaceView = MiniGdxSurfaceView(this, gameContext, gameFactory, activity)
        activity.setContentView(surfaceView)
    }

    override var postRenderLoop: () -> Unit = { }
}
