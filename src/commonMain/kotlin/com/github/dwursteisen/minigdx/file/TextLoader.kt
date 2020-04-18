package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.entity.primitives.Texture
import com.github.dwursteisen.minigdx.entity.text.AngelCode
import com.github.dwursteisen.minigdx.entity.text.Text
import com.github.dwursteisen.minigdx.fileHandler

class TextLoader : FileLoader<Text> {

    @ExperimentalStdlibApi
    override fun load(filename: String, handler: PlatformFileHandler): Content<Text> {
        val angelCode: Content<AngelCode> = fileHandler.get("$filename.fnt")
        val textureContent: Content<Texture> = fileHandler.get("$filename.png")

        return textureContent.flatMap { texture ->
            angelCode.map {
                Text(
                    angelCode = it,
                    fontSprite = texture
                )
            }
        }
    }
}
