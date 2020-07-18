package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.logger.Logger

expect class PlatformFileHandler {

    val logger: Logger

    fun read(filename: String): Content<String>

    fun readData(filename: String): Content<ByteArray>

    fun readTextureImage(filename: String): Content<TextureImage>
}
