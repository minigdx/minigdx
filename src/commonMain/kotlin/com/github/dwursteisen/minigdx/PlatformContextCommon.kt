package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.graphics.ViewportStrategy
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.logger.Logger

expect open class PlatformContextCommon(configuration: GameConfiguration) : PlatformContext {
    override fun createGL(): GL
    override fun createFileHandler(logger: Logger): FileHandler
    override fun createInputHandler(logger: Logger): InputHandler
    override fun createViewportStrategy(logger: Logger): ViewportStrategy
    override fun createLogger(): Logger
    override fun createOptions(): Options

    override fun start(gameContext: GameContext, gameFactory: (GameContext) -> Game)
}
