package com.github.dwursteisen.minigdx.file

import java.io.File

actual class PlatformFileHandler {

    actual fun read(filename: String): Content<String> {
        val content = Content<String>()
        content.load(File(filename).readText())
        return content
    }

    actual fun readData(filename: String): Content<ByteArray> {
        val content = Content<ByteArray>()
        content.load(File(filename).readBytes())
        return content
    }
}
