package com.github.dwursteisen.minigdx.file

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

// http://www.angelcode.com/products/bmfont/doc/file_format.html
data class AngelCharacter(
    val id: Char,
    /** The left position of the character image in the texture. */
    val x: Pixel,
    /** The top position of the character image in the texture. */
    val y: Pixel,
    /** The width of the character image in the texture. */
    val width: Pixel,
    /** The height of the character image in the texture. */
    val height: Pixel,
    /** How much the current position should be offset when copying the image from the texture to the screen */
    val xoffset: Pixel,
    /** How much the current position should be offset when copying the image from the texture to the screen. */
    val yoffset: Pixel,
    /** How much the current position should be advanced after drawing the character. */
    val xadvance: Pixel
)

class AngelCode(
    val info: FontInfo,
    val characters: Map<Char, AngelCharacter>
)
