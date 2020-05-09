package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.entity.models.Scene

class SceneLoader : FileLoader<Scene> {

    @ExperimentalStdlibApi
    private fun load(filename: String, content: ByteArray): Scene {
        val description = if (filename.endsWith(".json")) {
            ModelReader.fromJson(content)
        } else {
            ModelReader.fromProtobuf(content)
        }

        return Scene(
            models = mapOf("model" to description.model),
            camera = description.cameras
        )
    }

    @ExperimentalStdlibApi
    override fun load(filename: String, handler: PlatformFileHandler): Content<Scene> {
        return handler.readData(filename).map { load(filename, it) }
    }
}
