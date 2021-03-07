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
}
