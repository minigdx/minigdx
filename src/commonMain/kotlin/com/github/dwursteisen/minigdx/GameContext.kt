package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.graphics.GLResourceClient
import com.github.dwursteisen.minigdx.graphics.ViewportStrategy
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.logger.Logger

class Options(var debug: Boolean)

class GameContext(
    private val glContext: GLContext
) {
    val gl: GL = glContext.createContext()
    val logger: Logger = glContext.createLogger()

    val fileHandler: FileHandler = glContext.createFileHandler(logger)
    val input: InputHandler = glContext.createInputHandler(logger)
    val viewport: ViewportStrategy = glContext.createViewportStrategy(logger)
    val glResourceClient = GLResourceClient(gl, logger)

    val ratio = gl.screen.ratio
    val options = glContext.createOptions()

    fun execute(block: (GameContext) -> Game) {
        glContext.run(this, block)
    }
}
