package com.github.dwursteisen.minigdx.file

class TextureImageLoader : FileLoader<TextureImage> {

    override fun load(filename: String, handler: PlatformFileHandler): Content<TextureImage> {
        return handler.readTextureImage(filename)
    }
}
