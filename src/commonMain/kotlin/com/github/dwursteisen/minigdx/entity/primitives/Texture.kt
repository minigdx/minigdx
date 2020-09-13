package com.github.dwursteisen.minigdx.entity.primitives

import com.dwursteisen.minigdx.scene.api.common.Id
import com.github.dwursteisen.minigdx.file.TextureImage

class Texture(val source: TextureImage) {
    val id = Id()
    val width = source.width
    val height = source.height
}
