package com.github.dwursteisen.minigdx.graph

import com.dwursteisen.minigdx.scene.api.model.UV
import com.dwursteisen.minigdx.scene.api.sprite.SpriteAnimation
import com.github.dwursteisen.minigdx.file.Texture

class Sprite(
    val spriteSheet: Texture,
    val animations: Map<String, SpriteAnimation> = emptyMap(),
    val uvs: List<UV>
)
