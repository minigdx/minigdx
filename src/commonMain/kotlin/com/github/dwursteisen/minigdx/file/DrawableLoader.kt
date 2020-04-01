package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.entity.delegate.Drawable

class DrawableLoader : FileLoader<Drawable> {

    @ExperimentalStdlibApi
    override fun load(filename: String, content: String): Drawable {
        return load(filename, content.encodeToByteArray())
    }

    @ExperimentalStdlibApi
    override fun load(filename: String, content: ByteArray): Drawable {
        val (mesh, _, _) = if (filename.endsWith(".json")) {
            MeshReader.fromJson(content)
        } else {
            MeshReader.fromProtobuf(content)
        }
        return Drawable(mesh)
    }
}
