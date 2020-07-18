package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.graphics.ViewportStrategy
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.logger.Logger

expect class GLContext(configuration: GLConfiguration) {
    internal fun createContext(): GL
    internal fun createFileHandler(logger: Logger): FileHandler
    internal fun createInputHandler(logger: Logger): InputHandler
    internal fun createViewportStrategy(logger: Logger): ViewportStrategy
    internal fun createLogger(): Logger

    fun run(gameContext: GameContext, gameFactory: (GameContext) -> Game)
}
