package com.github.dwursteisen.minigdx.ecs.components

import ModelFactory.gameContext
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.file.AngelCharacter
import com.github.dwursteisen.minigdx.file.AngelCode
import com.github.dwursteisen.minigdx.file.Font
import com.github.dwursteisen.minigdx.file.FontInfo
import com.github.dwursteisen.minigdx.file.Texture
import createTextureImage
import kotlin.test.Test
import kotlin.test.assertEquals

class TextComponentTest {

    private val font = Font(
        angelCode = AngelCode(
            characters = mapOf(
                'a' to AngelCharacter(
                    id = 'a',
                    x = 0,
                    y = 0,
                    width = 10,
                    height = 10,
                    xoffset = 0,
                    yoffset = 0,
                    xadvance = 10
                )
            ),
            info = FontInfo("test-font", size = 32, lineHeight = 32, base = 32, pages = 0, "", charsCount = 1)
        ),
        fontSprite = Texture(source = createTextureImage())
    )

    @Test
    fun textComponent_it_create_text_top_left_aligned() {
        val entity = Entity(Engine(gameContext()))
        val component = TextComponent("a", font, lineWith = 2)

        entity.add(Position())
        entity.add(BoundingBox.default())
        entity.add(component)

        val textMeshData = component.generateVertices(1f)
        // 4 vertices for the letter 'a'
        assertEquals(4, textMeshData.vertices.size)
        assertEquals(-1f, textMeshData.vertices.first().position.x)
    }

    @Test
    fun textComponent_it_create_text_top_center_aligned() {
    }

    @Test
    fun textComponent_it_create_text_right_center_aligned() {
    }
}
