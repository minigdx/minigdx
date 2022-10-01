package com.github.dwursteisen.minigdx

expect class GameConfiguration {

    /**
     * Configuration of the game screen.
     *
     * Note that the game screen will be resized
     * regarding the current viewport strategy.
     */
    val gameScreenConfiguration: GameScreenConfiguration

    /**
     * Name of the game.
     */
    val gameName: String

    /**
     * Is debug mode turned on.
     */
    val debug: Boolean

    /**
     * Joint limit: the number of maximum joints that can
     * be loaded by the game engine.
     *
     * The default value is set so it can be loaded on every platform:
     * Web, desktop, Android and even Android TV. This last one has the
     * most limitation.
     *
     * If you're sure that your game will be loaded on a platform that can
     * manage more joints and if your assets require more joints, you can
     * increase this default limit.
     */
    val jointLimit: Int
}
