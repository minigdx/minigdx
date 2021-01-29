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
    text: String,
    var font: Font,
    var meshPrimitive: MeshPrimitive,
    var charactersPerLine: Int = text.split("\n").maxBy { it.length }?.length ?: text.length
) : Component {

    var currentCharactersPerLine: Int = charactersPerLine
        private set

    var currentLine: String = text
        private set

    var text: String = text
        set(value) {
            meshPrimitive.isUVDirty = true
            meshPrimitive.isDirty = true

            val longestLine = text.split("\n")
                .maxBy { it.length }
                ?: ""

            currentCharactersPerLine = min(longestLine.length, charactersPerLine)
            currentLine = longestLine.take(currentCharactersPerLine)

            generateMesh(meshPrimitive)

            field = value
        }

    init {
        this.text = text
    }

    class TextData(
        var currentX: Float = 0f,
        var currentY: Float = 0f,
        @Deprecated("to be removed. Use verticces instead")
        val vertisesPositions: FloatArray,
        @Deprecated("to be removed")
        val uvPositions: FloatArray,
        @Deprecated("to be removed")
        val vertisesOrder: ShortArray,
        val textureWidth: Float,
        val textureHeight: Float,
        var vertices: List<Vertex> = emptyList(),
        var verticesOrder: IntArray = IntArray(0)
    )

    fun currentLineInPixel(text: String, font: Font): Pixel {
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

    fun createMesh(scale: Float, text: String, font: Font): TextData {

        val textData = TextData(
            vertisesPositions = FloatArray(text.length * 3 * 4) { 0f },
            uvPositions = FloatArray(text.length * 2 * 4) { 0f },
            vertisesOrder = ShortArray(text.length * 6) { 0 },
            textureHeight = font.fontSprite.height.toFloat(),
            textureWidth = font.fontSprite.width.toFloat()
        )

        val angelCode = font.angelCode
        text.forEachIndexed { index, char ->
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

                    vertisesPositions[index * 3 * 4 + 0] = scale * xLeft
                    vertisesPositions[index * 3 * 4 + 1] = scale * yTop
                    vertisesPositions[index * 3 * 4 + 2] = 0f

                    vertisesPositions[index * 3 * 4 + 3] = scale * xLeft
                    vertisesPositions[index * 3 * 4 + 4] = scale * yBottom
                    vertisesPositions[index * 3 * 4 + 5] = 0f

                    vertisesPositions[index * 3 * 4 + 6] = scale * xRight
                    vertisesPositions[index * 3 * 4 + 7] = scale * yBottom
                    vertisesPositions[index * 3 * 4 + 8] = 0f

                    vertisesPositions[index * 3 * 4 + 9] = scale * xRight
                    vertisesPositions[index * 3 * 4 + 10] = scale * yTop
                    vertisesPositions[index * 3 * 4 + 11] = 0f

                    uvPositions[index * 2 * 4 + 0] = code.x / textureWidth
                    uvPositions[index * 2 * 4 + 1] = code.y / textureHeight

                    uvPositions[index * 2 * 4 + 2] = code.x / textureWidth
                    uvPositions[index * 2 * 4 + 3] = (code.y + code.height) / textureHeight

                    uvPositions[index * 2 * 4 + 4] = (code.x + code.width) / textureWidth
                    uvPositions[index * 2 * 4 + 5] = (code.y + code.height) / textureHeight

                    uvPositions[index * 2 * 4 + 6] = (code.x + code.width) / textureWidth
                    uvPositions[index * 2 * 4 + 7] = code.y / textureHeight

                    // lower triangle
                    vertisesOrder[index * 6 + 0] = (index * 4).toShort()
                    vertisesOrder[index * 6 + 1] = (index * 4 + 1).toShort()
                    vertisesOrder[index * 6 + 2] = (index * 4 + 2).toShort()
                    // upper triangle
                    vertisesOrder[index * 6 + 3] = (index * 4).toShort()
                    vertisesOrder[index * 6 + 4] = (index * 4 + 2).toShort()
                    vertisesOrder[index * 6 + 5] = (index * 4 + 3).toShort()

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
                        index * 4 + 0,
                        index * 4 + 1,
                        index * 4 + 2,
                        index * 4 + 0,
                        index * 4 + 2,
                        index * 4 + 3
                    )

                    currentX += code.xadvance + code.xoffset
                }
            }
        }
        return textData
    }

    private fun generateMesh(meshPrimitive: MeshPrimitive) {
        val lineInPixel: Pixel = currentLineInPixel(currentLine, font)
        val lineInWorldUnit = (1f / charactersPerLine) * currentCharactersPerLine
        val scale = lineInWorldUnit / lineInPixel.toFloat()
        val textData = createMesh(scale, text, font)

        meshPrimitive.primitive.vertices = textData.vertices
        meshPrimitive.primitive.verticesOrder = textData.verticesOrder
    }
}
