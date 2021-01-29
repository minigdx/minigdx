package com.github.dwursteisen.minigdx.file

import com.dwursteisen.minigdx.scene.api.common.Id

class Texture(val source: TextureImage) {
    val id = Id()
    val width = source.width
    val height = source.height
}
