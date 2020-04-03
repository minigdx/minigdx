package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.entity.delegate.Model

class ModelLoader : FileLoader<Model> {

    @ExperimentalStdlibApi
    override fun load(filename: String, content: String): Model {
        return load(filename, content.encodeToByteArray())
    }

    @ExperimentalStdlibApi
    override fun load(filename: String, content: ByteArray): Model {
        val description = if (filename.endsWith(".json")) {
            ModelReader.fromJson(content)
        } else {
            ModelReader.fromProtobuf(content)
        }
        return description.model
    }
}
