package com.github.dwursteisen.minigdx

actual class GameConfiguration(
    val name: String,
    actual val gameName: String,
    val width: Pixels,
    val height: Pixels,
    actual val debug: Boolean = false
)
