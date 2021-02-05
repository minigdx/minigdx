package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.game.Game
import com.github.dwursteisen.minigdx.graphics.ViewportStrategy
import com.github.dwursteisen.minigdx.input.InputHandler
import com.github.dwursteisen.minigdx.logger.Logger

interface PlatformContext {
    /**
     * Configuration used to create the game and the platform.
     */
    val configuration: GameConfiguration
    /**
     * Create the GL object, used for communicating with the GL Driver.
     */
    fun createGL(): GL

    /**
     * Create the File Handler: the entry point to access file on disk, ...
     */
    fun createFileHandler(logger: Logger): FileHandler

    /**
     * Create the Input Handler: it's the entry point to manage game inputs (keyboard, touch, mouse)
     */
    fun createInputHandler(logger: Logger): InputHandler

    /**
     * Create the viewport Strategy  responsible to get the displayed grahical area.
     */
    fun createViewportStrategy(logger: Logger): ViewportStrategy

    /**
     * Create the logger
     */
    fun createLogger(): Logger

    /**
     * Create the game options
     */
    fun createOptions(): Options

    /**
     * Start the game using the platform specific creation code.
     * The game will be created by [gameFactory] using the [gameContext]
     * created in by the platform.
     */
    fun start(gameFactory: (GameContext) -> Game)
}
