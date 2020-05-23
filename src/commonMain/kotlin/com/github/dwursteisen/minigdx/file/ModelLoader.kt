package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.entity.delegate.Drawable

class ModelLoader : FileLoader<Drawable> {

    @ExperimentalStdlibApi
    private fun load(filename: String, content: ByteArray): Drawable {
        val description = if (filename.endsWith(".json")) {
            ModelReader.fromJson(content)
        } else {
            ModelReader.fromProtobuf(content)
        }
        return description.model
    }

    @ExperimentalStdlibApi
    override fun load(filename: String, handler: PlatformFileHandler): Content<Drawable> {
        return handler.readData(filename).map { load(filename, it) }
    }
}
