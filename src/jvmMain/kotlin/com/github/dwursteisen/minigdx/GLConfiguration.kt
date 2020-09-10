package com.github.dwursteisen.minigdx

actual class GLConfiguration(
    val name: String,
    actual val gameName: String,
    val width: Pixels,
    val height: Pixels,
    actual val debug: Boolean = false
)
