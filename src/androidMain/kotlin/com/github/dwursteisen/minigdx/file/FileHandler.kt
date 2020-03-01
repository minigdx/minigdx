package com.github.dwursteisen.minigdx.file

import android.content.Context

class SyncContent<T>(val content: T) : Content<T> {
    override fun onLoaded(block: (T) -> Unit) {
        block(content)
    }
}

actual class FileHandler(private val context: Context) {

    private var loaded = 0
    private var toLoad = 0

    @ExperimentalStdlibApi
    actual fun read(fileName: String): Content<String> {
        toLoad++
        val data = context.assets.open(fileName).readBytes().decodeToString()
        loaded++
        return SyncContent(data)
    }

    actual fun readData(filename: String): Content<ByteArray> {
        toLoad++
        val data = context.assets.open(filename).readBytes()
        loaded++
        return SyncContent(data)
    }

    actual val isLoaded: Boolean = loaded == toLoad

    actual val loadProgression: Float = loaded / toLoad.toFloat()
}
