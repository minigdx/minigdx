package com.github.dwursteisen.minigdx.entity.text

import com.github.dwursteisen.minigdx.Pixel

data class FontInfo(
    val face: String,
    val size: Pixel,
    val lineHeight: Pixel,
    val base: Pixel,
    val pages: Int,
    val fontFile: String,
    val charsCount: Int
)

data class AngelCharacter(
    val id: Char,
    val x: Pixel,
    val y: Pixel,
    val width: Pixel,
    val height: Pixel,
    val xoffset: Pixel,
    val yoffset: Pixel,
    val xadvance: Pixel
)

class AngelCode(
    val info: FontInfo,
    val characters: Map<Char, AngelCharacter>
)
