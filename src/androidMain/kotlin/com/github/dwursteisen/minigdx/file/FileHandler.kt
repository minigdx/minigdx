package com.github.dwursteisen.minigdx.file

import android.content.Context
import com.github.dwursteisen.minigdx.log

class SyncContent<T>(val content: T) : Content<T> {
    override fun onLoaded(block: (T) -> Unit) {
        block(content)
    }
}

actual class FileHandler(private val context: Context) {

    private var loaded = 0
    private var toLoad = 0

    @ExperimentalStdlibApi
    actual fun read(filename: String): Content<String> {
        toLoad++
        val data = context.assets.open(filename).readBytes().decodeToString()
        loaded++
        log.info("FILE_HANDLER") { "Reading '$filename' as String content" }
        return SyncContent(data)
    }

    actual fun readData(filename: String): Content<ByteArray> {
        toLoad++
        val data = context.assets.open(filename).readBytes()
        loaded++
        log.info("FILE_HANDLER") { "Reading '$filename' as Byte content" }
        return SyncContent(data)
    }

    actual val isLoaded: Boolean = loaded == toLoad

    actual val loadProgression: Float = loaded / toLoad.toFloat()
}
