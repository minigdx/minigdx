package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.graphics.ViewportStrategy
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.logger.Logger

interface PlatformContext {
    fun createGL(): GL
    fun createFileHandler(logger: Logger): FileHandler
    fun createInputHandler(logger: Logger): InputHandler
    fun createViewportStrategy(logger: Logger): ViewportStrategy
    fun createLogger(): Logger
    fun createOptions(): Options

    /**
     * Start the game using the platform specific creation code.
     * The game will be created by [gameFactory] using the [gameContext].
     */
    fun start(gameContext: GameContext, gameFactory: (GameContext) -> Game)
}
