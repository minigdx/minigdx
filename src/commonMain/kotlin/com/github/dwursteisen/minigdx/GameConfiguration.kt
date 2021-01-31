package com.github.dwursteisen.minigdx

expect class GameConfiguration {

    /**
     * Name of the game
     */
    val gameName: String

    /**
     * Is debug mode turned on
     */
    val debug: Boolean
}
