package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.graphics.ViewportStrategy
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.logger.Logger

expect open class GLContext(configuration: GLConfiguration) {
    internal open fun createContext(): GL
    internal open fun createFileHandler(logger: Logger): FileHandler
    internal open fun createInputHandler(logger: Logger): InputHandler
    internal open fun createViewportStrategy(logger: Logger): ViewportStrategy
    internal open fun createLogger(): Logger
    internal open fun createOptions(): Options

    open fun run(gameContext: GameContext, gameFactory: (GameContext) -> Game)
}
