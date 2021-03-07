package com.github.dwursteisen.minigdx.file

class Font(
    val angelCode: AngelCode,
    val fontSprite: Texture
) {

    operator fun get(char: Char): AngelCharacter {
        return angelCode.characters.getOrElse(char) {
            angelCode.characters[' '] ?: angelCode.characters['a'] ?: angelCode.characters.values.first()
        }
    }
}
