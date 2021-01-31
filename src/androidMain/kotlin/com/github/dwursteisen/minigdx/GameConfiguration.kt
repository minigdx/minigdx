package com.github.dwursteisen.minigdx

actual class GameConfiguration(
    actual val gameName: String,
    actual val debug: Boolean,
    val activity: MiniGdxActivity?
)
