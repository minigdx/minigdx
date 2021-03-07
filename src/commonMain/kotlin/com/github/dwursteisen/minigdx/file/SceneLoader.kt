package com.github.dwursteisen.minigdx.file

import com.dwursteisen.minigdx.scene.api.Scene

class SceneLoader : FileLoader<Scene> {

    @ExperimentalStdlibApi
    private fun load(filename: String, content: ByteArray): Scene {
        return if (filename.endsWith(".json")) {
            Scene.readJson(content)
        } else {
            Scene.readProtobuf(content)
        }
    }

    @ExperimentalStdlibApi
    override fun load(filename: String, handler: FileHandler): Content<Scene> {
        return handler.readData(filename).map { load(filename, it) }
    }
}
