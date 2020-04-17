package com.github.dwursteisen.minigdx.file

import java.nio.ByteBuffer

actual class TextureImage(
    actual val width: Int,
    actual val height: Int,
    val glFormat: Int,
    val glType: Int,
    val pixels: ByteBuffer
)
