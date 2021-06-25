package com.github.dwursteisen.minigdx.file

import com.dwursteisen.minigdx.scene.api.common.Id

class TextureLoader : FileLoader<Texture> {

    private val textureImageLoader = TextureImageLoader()

    override fun load(filename: String, handler: FileHandler): Content<Texture> {
        val result  = Texture(
            Id(),
            byteArrayOf(
            0xFF.toByte(), 0x00.toByte(), 0x00.toByte(), 0xFF.toByte(),
            0x00.toByte(), 0xFF.toByte(), 0x00.toByte(), 0xFF.toByte(),
            0x00.toByte(), 0x00.toByte(), 0xFF.toByte(), 0xFF.toByte(),
            0xFF.toByte(), 0xFF.toByte(), 0x00.toByte(), 0xFF.toByte()
        ), 2, 2, hasAlpha = false)

        val content = handler.create(filename, result)

        textureImageLoader.load(filename, handler).onLoaded { textureImage  ->
            result.textureImage = textureImage
            result.height = textureImage.height
            result.width = textureImage.width
        }
        return content
    }
}
