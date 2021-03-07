package com.github.dwursteisen.minigdx

actual class GameConfiguration(
    actual val gameName: String,
    actual val gameScreenConfiguration: GameScreenConfiguration,
    actual val debug: Boolean = false,
    /**
     * Configuration of the application window.
     */
    val window: Window,
    /**
     * Run the game using wireframe as display mode
     * instead of solid polygons.
     */
    val wireframe: Boolean = false
)
