package com.github.dwursteisen.minigdx.file

interface Content<T> {

    fun onLoaded(block: (T) -> Unit)
}

expect class FileHandler {

    fun read(filename: String): Content<String>

    fun readData(filename: String): Content<ByteArray>

    val isLoaded: Boolean

    val loadProgression: Float
}
