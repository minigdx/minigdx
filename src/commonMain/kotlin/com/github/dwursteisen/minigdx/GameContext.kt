package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.graphics.GLResourceClient
import com.github.dwursteisen.minigdx.graphics.ViewportStrategy
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.logger.Logger

class Options(var debug: Boolean)

class GameScreen(val width: Pixel, val height: Pixel, val ratio: Ratio = width / height.toFloat())

class DeviceScreen(var width: Pixel, var height: Pixel)

class GameContext(
    platformContext: PlatformContext,
    gameResolution: Resolution
) {
    val gl: GL = platformContext.createGL()
    val logger: Logger = platformContext.createLogger()

    val fileHandler: FileHandler = platformContext.createFileHandler(logger)
    val input: InputHandler = platformContext.createInputHandler(logger)
    val viewport: ViewportStrategy = platformContext.createViewportStrategy(logger)
    val glResourceClient = GLResourceClient(gl, logger)

    val gameScreen: GameScreen = GameScreen(
        gameResolution.width,
        gameResolution.height,
        gameResolution.ratio
    )

    val deviceScreen: DeviceScreen = DeviceScreen(
        gameResolution.width,
        gameResolution.height
    )

    val options = platformContext.createOptions()
}
