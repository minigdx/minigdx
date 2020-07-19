package com.github.dwursteisen.minigdx.file

class TextureImageLoader : FileLoader<TextureImage> {

    override fun load(filename: String, handler: FileHandler): Content<TextureImage> {
        return handler.platformFileHandler.readTextureImage(filename)
    }
}
