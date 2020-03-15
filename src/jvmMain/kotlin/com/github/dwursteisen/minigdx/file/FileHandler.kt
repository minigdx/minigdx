package com.github.dwursteisen.minigdx.file

import java.io.File

class SyncContent<T>(val content: T) : Content<T> {

    override fun onLoaded(block: (T) -> Unit) {
        block(content)
    }
}

actual class FileHandler {

    private var total = 0
    private var loaded = 0

    actual fun read(filename: String): Content<String> {
        total++
        val content = File(filename).readText()
        loaded++
        return SyncContent(content)
    }

    actual fun readData(filename: String): Content<ByteArray> {
        total++
        val content = File(filename).readBytes()
        loaded++
        return SyncContent(content)
    }

    actual val isLoaded: Boolean = total == loaded

    actual val loadProgression: Float = loaded / total.toFloat()
}
