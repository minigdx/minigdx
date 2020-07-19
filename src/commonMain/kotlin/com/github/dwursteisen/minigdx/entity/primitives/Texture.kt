package com.github.dwursteisen.minigdx.entity.primitives

import com.github.dwursteisen.minigdx.file.TextureImage

class Texture(val source: TextureImage) {

    val width = source.width
    val height = source.height
}
