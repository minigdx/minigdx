package com.github.dwursteisen.minigdx.file

expect class PlatformFileHandler {

    fun read(filename: String): Content<String>

    fun readData(filename: String): Content<ByteArray>
}
