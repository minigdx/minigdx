package com.github.dwursteisen.minigdx.file

import org.khronos.webgl.TexImageSource

actual class TextureImage(
    val source: TexImageSource,
    actual val width: Int,
    actual val height: Int
)
