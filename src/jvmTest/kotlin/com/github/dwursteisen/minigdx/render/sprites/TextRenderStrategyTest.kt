package com.github.dwursteisen.minigdx.render.sprites

import com.github.dwursteisen.minigdx.entity.primitives.Texture
import com.github.dwursteisen.minigdx.entity.text.AngelCode
import com.github.dwursteisen.minigdx.entity.text.Font
import com.github.dwursteisen.minigdx.file.AngelCodeLoader
import com.github.dwursteisen.minigdx.file.FileHandler
import com.github.dwursteisen.minigdx.file.FontLoader
import com.github.dwursteisen.minigdx.file.PlatformFileHandler
import com.github.dwursteisen.minigdx.file.TextureLoader
import com.github.dwursteisen.minigdx.math.Vector2
import com.github.dwursteisen.minigdx.math.Vector3
import org.junit.Assert.assertEquals
import org.junit.Test

class TextRenderStrategyTest {

    @Test
    fun generateData() {
        val fileHandler = FileHandler(PlatformFileHandler(), loaders = mapOf(
            Font::class to FontLoader(),
            Texture::class to TextureLoader(),
            AngelCode::class to AngelCodeLoader()
        ))

        val font: Font by fileHandler.get("src/jvmTest/resources/pt_font", Font::class)
        val result = TextRenderStrategy.generateTextDate("abcd", font)

        val positions = result.vertisesPositions
            .toList()
            .chunked(3)
            .map { Vector3(it[0], it[1], it[2]) }

        val uvs = result.uvPositions
            .toList()
            .chunked(2)
            .map { Vector2(it[0], it[1]) }

        fun Int.ratioW(): Float {
            return this.toFloat() / font.fontSprite.width.toFloat()
        }

        fun Int.ratioH(): Float {
            return this.toFloat() / font.fontSprite.height.toFloat()
        }

        // Vertices letter 'a'
        assertEquals(Vector3(0f, -12f, -0.1f), positions[0])
        assertEquals(Vector3(0f, -35f, -0.1f), positions[1])
        assertEquals(Vector3(20f, -35f, -0.1f), positions[2])
        assertEquals(Vector3(20f, -12f, -0.1f), positions[3])

        // Vertices letter 'b'
        assertEquals(Vector3(22f, -6f, -0.1f), positions[4])
        assertEquals(Vector3(22f, -35f, -0.1f), positions[5])
        assertEquals(Vector3(43f, -35f, -0.1f), positions[6])
        assertEquals(Vector3(43f, -6f, -0.1f), positions[7])

        // UV of a
        assertEquals(Vector2(357.ratioW(), 66.ratioH()), uvs[0])
        assertEquals(Vector2(357.ratioW(), 89.ratioH()), uvs[1])
        assertEquals(Vector2(377.ratioW(), 89.ratioH()), uvs[2])
        assertEquals(Vector2(377.ratioW(), 66.ratioH()), uvs[3])

        // Order
        assertEquals(0, result.vertisesOrder[0].toInt())
        assertEquals(1, result.vertisesOrder[1].toInt())
        assertEquals(2, result.vertisesOrder[2].toInt())
        assertEquals(0, result.vertisesOrder[3].toInt())
        assertEquals(2, result.vertisesOrder[4].toInt())
        assertEquals(3, result.vertisesOrder[5].toInt())
    }
}
