package com.github.dwursteisen.minigdx.render.sprites

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.ecs.components.Text
import com.github.dwursteisen.minigdx.ecs.components.gl.SpritePrimitive
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.entity.text.Font
import com.github.dwursteisen.minigdx.shaders.DataSource

object TextRenderStrategy : RenderStrategy {

    class TextData(
        var currentX: Float = 0f,
        var currentY: Float = 0f,
        val vertisesPositions: FloatArray,
        val uvPositions: FloatArray,
        val vertisesOrder: ShortArray,
        val textureWidth: Float,
        val textureHeight: Float
    )

    override fun render(gl: GL, entity: Entity) {
        val text = entity.get(Text::class)
        val spritePrimitive = entity.get(SpritePrimitive::class)

        gl.bindTexture(GL.TEXTURE_2D, spritePrimitive.textureReference!!)
        gl.enable(GL.BLEND)
        gl.blendFunc(GL.SRC_ALPHA, GL.ONE_MINUS_SRC_ALPHA)

        if (text.text != text.previousText) {
            val textData = generateTextDate(text.text, text.font)
            val vertices = textData.vertisesPositions
            val uv = textData.uvPositions
            val order = textData.vertisesOrder

            gl.bindBuffer(GL.ARRAY_BUFFER, spritePrimitive.verticesBuffer!!)
            gl.bufferData(
                GL.ARRAY_BUFFER,
                DataSource.FloatDataSource(vertices),
                GL.STATIC_DRAW
            )

            gl.bindBuffer(GL.ARRAY_BUFFER, spritePrimitive.uvBuffer!!)
            gl.bufferData(
                GL.ARRAY_BUFFER,
                DataSource.FloatDataSource(uv),
                GL.STATIC_DRAW
            )

            gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, spritePrimitive.verticesOrderBuffer!!)
            gl.bufferData(
                target = GL.ELEMENT_ARRAY_BUFFER, data = DataSource.ShortDataSource(
                    order
                ), usage = GL.STATIC_DRAW
            )
            text.previousText = text.text
            spritePrimitive.numberOfIndices = order.size
        }

        gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, spritePrimitive.verticesOrderBuffer!!)
        gl.drawElements(
            GL.TRIANGLES, spritePrimitive.numberOfIndices,
            GL.UNSIGNED_SHORT, 0
        )
        gl.disable(GL.BLEND)
    }

    fun generateTextDate(text: String, font: Font): TextData {
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

                    vertisesPositions[index * 3 * 4 + 0] = xLeft
                    vertisesPositions[index * 3 * 4 + 1] = yTop
                    vertisesPositions[index * 3 * 4 + 2] = -0.1f

                    vertisesPositions[index * 3 * 4 + 3] = xLeft
                    vertisesPositions[index * 3 * 4 + 4] = yBottom
                    vertisesPositions[index * 3 * 4 + 5] = -0.1f

                    vertisesPositions[index * 3 * 4 + 6] = xRight
                    vertisesPositions[index * 3 * 4 + 7] = yBottom
                    vertisesPositions[index * 3 * 4 + 8] = -0.1f

                    vertisesPositions[index * 3 * 4 + 9] = xRight
                    vertisesPositions[index * 3 * 4 + 10] = yTop
                    vertisesPositions[index * 3 * 4 + 11] = -0.1f

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

                    currentX += code.xadvance
                }
            }
        }
        return textData
    }
}
