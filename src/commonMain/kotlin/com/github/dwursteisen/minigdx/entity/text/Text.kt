package com.github.dwursteisen.minigdx.entity.text

import com.github.dwursteisen.minigdx.entity.CanDraw
import com.github.dwursteisen.minigdx.entity.CanMove
import com.github.dwursteisen.minigdx.entity.delegate.Movable
import com.github.dwursteisen.minigdx.entity.primitives.Texture
import com.github.dwursteisen.minigdx.log
import com.github.dwursteisen.minigdx.shaders.ShaderProgram

class Text(
    var text: String = "",
    var size: Float = 16f,
    private val angelCode: AngelCode,
    private val fontSprite: Texture
) : CanMove by Movable(), CanDraw {

    private var xOffset = 0f
    private var yOffset = 0f

    override fun draw(shader: ShaderProgram) {
        xOffset = 0f
        yOffset = 0f
        text.forEach { char ->
            drawCharacter(shader, char)
        }
    }

    // TODO: convert font size from the world to size from the font?
    private fun drawCharacter(shader: ShaderProgram, char: Char) {
        if (char == '\n') {
            xOffset = 0f
            yOffset += size
            return
        }

        val code = angelCode.characters[char]
            ?: return log.warn("TEXT") { "Character '$char' missing from the font '${angelCode.info.fontFile}'" }

        fontSprite.draw(
            shader,
            position.x + xOffset, position.y + yOffset,
            size, size,
            code.x, code.y,
            code.width, code.height
        )

        xOffset += size
    }
}
