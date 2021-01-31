package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.graphics.GLResourceClient
import com.github.dwursteisen.minigdx.graphics.ViewportStrategy
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.logger.Logger

class Options(var debug: Boolean)

class GameContext(
    platformContext: PlatformContext
) {
    val gl: GL = platformContext.createGL()
    val logger: Logger = platformContext.createLogger()

    val fileHandler: FileHandler = platformContext.createFileHandler(logger)
    val input: InputHandler = platformContext.createInputHandler(logger)
    val viewport: ViewportStrategy = platformContext.createViewportStrategy(logger)
    val glResourceClient = GLResourceClient(gl, logger)

    val ratio = gl.screen.ratio
    val options = platformContext.createOptions()
}
