package com.github.dwursteisen.minigdx.file

class SoundLoader : FileLoader<Sound> {
    override fun load(filename: String, handler: FileHandler): Content<Sound> {
        return handler.platformFileHandler.readSound(filename)
    }
}
