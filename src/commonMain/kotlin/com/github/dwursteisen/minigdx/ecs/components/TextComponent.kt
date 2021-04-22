package com.github.dwursteisen.minigdx.ecs.components

import com.dwursteisen.minigdx.scene.api.model.Normal
import com.dwursteisen.minigdx.scene.api.model.Position
import com.dwursteisen.minigdx.scene.api.model.UV
import com.dwursteisen.minigdx.scene.api.model.Vertex
import com.github.dwursteisen.minigdx.Pixel
import com.github.dwursteisen.minigdx.ecs.components.gl.MeshPrimitive
import com.github.dwursteisen.minigdx.file.Font
import kotlin.math.min

class TextComponent(
    /**
     * Text to display by the component.
     */
    text: String,
    var font: Font,
    var meshPrimitive: MeshPrimitive,
    var charactersPerLine: Int = text.split("\n").maxByOrNull { it.length }?.length ?: text.length
) : Component {

    var currentCharactersPerLine: Int = charactersPerLine
        private set

    var currentLine: String = text
        private set

    var text: String = ""
        set(value) {
            field = value

            meshPrimitive.isUVDirty = true
            meshPrimitive.isDirty = true

            val longestLine = value.split("\n")
                .maxByOrNull { it.length }
                ?: ""

            currentCharactersPerLine = min(longestLine.length, charactersPerLine)
            currentLine = longestLine.take(currentCharactersPerLine)

            generateMesh(meshPrimitive)
        }

    init {
        // Force the setter to get called
        this.text = text
    }

    class TextData(
        var currentX: Float = 0f,
        var currentY: Float = 0f,
        val textureWidth: Float,
        val textureHeight: Float,
        var vertices: List<Vertex> = emptyList(),
        var verticesOrder: IntArray = IntArray(0)
    )

    /**
     * Compute the length of the [text] in pixel using the [font].
     *
     * The text should be only one line otherwise the length will be cumulative.
     */
    private fun currentLineInPixel(text: String, font: Font): Pixel {
        return text.mapIndexed { index, char ->
            val angelCharacter = font[char]
            var xAdvance = angelCharacter.xadvance
            xAdvance += if (index == 0) {
                0
            } else {
                angelCharacter.xoffset
            }
            xAdvance
        }.sum()
    }

    private fun createMesh(scale: Float, text: String, font: Font): TextData {

        val textData = TextData(
            textureHeight = font.fontSprite.height.toFloat(),
            textureWidth = font.fontSprite.width.toFloat()
        )

        val angelCode = font.angelCode
        var characterIndex = 0
        text.forEach { char ->
            with(textData) {
                if (char == '\n') {
                    currentX = 0f
                    currentY -= angelCode.info.lineHeight
                } else if (char == ' ') {
                    currentX += angelCode.characters[char]?.width ?: angelCode.characters['a']?.width ?: 0
                }

                val code = angelCode.characters[char]
                if (code != null) {
                    val yTop = currentY - code.yoffset
                    val xLeft = currentX + code.xoffset
                    val xRight = xLeft + code.width
                    val yBottom = yTop - code.height

                    // up left
                    val a = Vertex(
                        Position(
                            scale * xLeft,
                            scale * yTop,
                            0f
                        ),
                        Normal(0f, 0f, 0f),
                        uv = UV(
                            code.x / textureWidth,
                            code.y / textureHeight
                        )
                    )
                    val b = Vertex(
                        Position(
                            scale * xLeft,
                            scale * yBottom,
                            0f
                        ),
                        Normal(0f, 0f, 0f),
                        uv = UV(
                            code.x / textureWidth,
                            (code.y + code.height) / textureHeight
                        )
                    )

                    val c = Vertex(
                        Position(
                            scale * xRight,
                            scale * yBottom,
                            0f
                        ),
                        Normal(0f, 0f, 0f),
                        uv = UV(
                            (code.x + code.width) / textureWidth,
                            (code.y + code.height) / textureHeight
                        )
                    )

                    val d = Vertex(
                        Position(
                            scale * xRight,
                            scale * yTop,
                            0f
                        ),
                        Normal(0f, 0f, 0f),
                        uv = UV(
                            (code.x + code.width) / textureWidth,
                            code.y / textureHeight
                        )
                    )

                    vertices += listOf(a, b, c, d)
                    verticesOrder += intArrayOf(
                        characterIndex * 4 + 0,
                        characterIndex * 4 + 1,
                        characterIndex * 4 + 2,
                        characterIndex * 4 + 0,
                        characterIndex * 4 + 2,
                        characterIndex * 4 + 3
                    )

                    currentX += code.xadvance + code.xoffset
                    characterIndex++
                }
            }
        }
        return textData
    }

    /**
     * Generate the mesh corresponding to the current text.
     *
     * Each characters will be assigned two triangles.
     */
    private fun generateMesh(meshPrimitive: MeshPrimitive) {
        val lineInPixel: Pixel = currentLineInPixel(currentLine, font)
        val lineInWorldUnit = (1f / charactersPerLine) * currentCharactersPerLine
        val scale = lineInWorldUnit / lineInPixel.toFloat()
        val textData = createMesh(scale, text, font)

        meshPrimitive.primitive.vertices = textData.vertices
        meshPrimitive.primitive.verticesOrder = textData.verticesOrder
    }
}
