package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.entity.primitives.Texture

class TextureLoader : FileLoader<Texture> {

    private val textureImageLoader = TextureImageLoader()

    override fun load(filename: String, handler: PlatformFileHandler): Content<Texture> {
        return textureImageLoader.load(filename, handler).map { Texture(it) }
    }
}
