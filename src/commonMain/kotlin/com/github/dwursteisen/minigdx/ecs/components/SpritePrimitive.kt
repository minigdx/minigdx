package com.github.dwursteisen.minigdx.ecs.components

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.buffer.Buffer
import com.github.dwursteisen.minigdx.buffer.DataSource
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.entity.primitives.Texture
import com.github.dwursteisen.minigdx.entity.text.AngelCode
import com.github.dwursteisen.minigdx.log
import com.github.dwursteisen.minigdx.shaders.TextureReference

class SpritePrimitive(
    val texture: Texture,
    val x: Int = 0,
    val y: Int = 0,
    val width: Int = texture.width,
    val height: Int = texture.height,
    val renderStrategy: RenderStrategy,
    // -- GL specific data -- //
    var isCompiled: Boolean = false,
    var verticesBuffer: Buffer? = null,
    var uvBuffer: Buffer? = null,
    var verticesOrderBuffer: Buffer? = null,
    var textureReference: TextureReference? = null
) : Component {

    interface RenderStrategy {

        fun render(gl: GL, entity: Entity)
    }

    class SpriteRenderStrategy : RenderStrategy {
        override fun render(gl: GL, entity: Entity) {
            TODO("Not yet implemented")
        }
    }

    object TextRenderStrategy : RenderStrategy {

        private class TextData(
            var xOffset: Float = 0f,
            var yOffset: Float = 0f,
            val vertisesPositions: FloatArray,
            val uvPositions: FloatArray,
            val vertisesOrder: ShortArray,
            val textureWidth: Float,
            val textureHeight: Float
        )

        override fun render(gl: GL, entity: Entity) {
            val component = entity.get(Text::class)
            val spritePrimitive = entity.get(SpritePrimitive::class)
            val text = component.text
            val textData = TextData(
                vertisesPositions = FloatArray(text.length * 3 * 4) { 0f },
                uvPositions = FloatArray(text.length * 2 * 4) { 0f },
                vertisesOrder = ShortArray(text.length * 6) { 0 },
                textureHeight = component.fontSprite.height.toFloat(),
                textureWidth = component.fontSprite.width.toFloat()
            )

            val angelCode = component.angelCode
            text.forEachIndexed { index, char ->
                drawCharacter(
                    index,
                    angelCode,
                    char,
                    textData
                )
            }

            val vertices = floatArrayOf(
                0f, 0f, -0.5f,
                1000f, 0f, -0.5f,
                0f, 500f, -0.5f
            )
            val order = shortArrayOf(0, 1, 2)

            val uv = floatArrayOf(
                0f, 0f,
                1f, 0f,
                0f, 0.3f
            )

            gl.bindTexture(GL.TEXTURE_2D, spritePrimitive.textureReference!!)
            gl.bindBuffer(GL.ARRAY_BUFFER, spritePrimitive.verticesBuffer!!)
            gl.bufferData(GL.ARRAY_BUFFER, DataSource.FloatDataSource(vertices), GL.STATIC_DRAW)

            gl.bindBuffer(GL.ARRAY_BUFFER, spritePrimitive.uvBuffer!!)
            gl.bufferData(GL.ARRAY_BUFFER, DataSource.FloatDataSource(uv), GL.STATIC_DRAW)

            gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, spritePrimitive.verticesOrderBuffer!!)
            gl.bufferData(target = GL.ELEMENT_ARRAY_BUFFER, data = DataSource.ShortDataSource(order), usage = GL.STATIC_DRAW)

            gl.drawElements(GL.TRIANGLES, order.size, GL.UNSIGNED_SHORT, 0)
        }

        private fun drawCharacter(
            index: Int,
            angelCode: AngelCode,
            char: Char,
            position: TextData
        ) {
            val scaleH = 1f
            val scaleW = 1f

            with(position) {
                if (char == '\n') {
                    xOffset = 0f
                    yOffset -= angelCode.info.lineHeight * scaleH
                    return
                } else if (char == ' ') {
                    xOffset += angelCode.info.base * scaleW
                }

                val code = angelCode.characters[char]
                    ?: return log.warn("TEXT") { "Character '$char' missing from the font '${angelCode.info.fontFile}'" }

                val previousXOffset = xOffset
                xOffset += (code.xadvance + code.xoffset) * scaleW

                vertisesPositions[index * 3 + 0] = previousXOffset
                vertisesPositions[index * 3 + 1] = yOffset
                vertisesPositions[index * 3 + 2] = 0f

                vertisesPositions[index * 3 + 3] = previousXOffset
                vertisesPositions[index * 3 + 4] = yOffset - code.yoffset * scaleH
                vertisesPositions[index * 3 + 5] = 0f

                vertisesPositions[index * 3 + 6] = xOffset
                vertisesPositions[index * 3 + 7] = yOffset
                vertisesPositions[index * 3 + 8] = 0f

                vertisesPositions[index * 3 + 9] = xOffset
                vertisesPositions[index * 3 + 10] = yOffset - code.yoffset * scaleH
                vertisesPositions[index * 3 + 11] = 0f

                uvPositions[index * 2 + 0] = code.x / textureWidth
                uvPositions[index * 2 + 1] = code.y / textureHeight
                uvPositions[index * 2 + 2] = (code.x + code.width) / textureWidth
                uvPositions[index * 2 + 3] = (code.y + code.height) / textureHeight

                // upper triangle
                vertisesOrder[index * 6 + 0] = index.toShort()
                vertisesOrder[index * 6 + 1] = (index + 1).toShort()
                vertisesOrder[index * 6 + 2] = (index + 2).toShort()
                // lower triangle
                vertisesOrder[index * 6 + 3] = (index + 1).toShort()
                vertisesOrder[index * 6 + 4] = (index + 2).toShort()
                vertisesOrder[index * 6 + 5] = (index + 3).toShort()
            }
        }
    }
}

class Text(
    var text: String,
    val angelCode: AngelCode,
    val fontSprite: Texture
) : Component
