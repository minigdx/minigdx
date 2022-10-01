package com.github.dwursteisen.minigdx

actual class GameConfiguration(
    actual val gameName: String,
    /**
     * Configuration of the game screen.
     *
     * Note that the game screen will be resized
     * regarding the current viewport strategy.
     */
    actual val gameScreenConfiguration: GameScreenConfiguration,
    actual val debug: Boolean,
    actual val jointLimit: Int = 50,
    val activity: MiniGdxActivity? = null
)
