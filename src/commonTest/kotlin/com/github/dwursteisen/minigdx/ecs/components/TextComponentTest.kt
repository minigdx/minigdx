package com.github.dwursteisen.minigdx.ecs.components

import ModelFactory.gameContext
import com.dwursteisen.minigdx.scene.api.common.Id
import com.dwursteisen.minigdx.scene.api.model.Primitive
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.file.AngelCharacter
import com.github.dwursteisen.minigdx.file.AngelCode
import com.github.dwursteisen.minigdx.file.Font
import com.github.dwursteisen.minigdx.file.FontInfo
import com.github.dwursteisen.minigdx.file.Texture
import com.github.dwursteisen.minigdx.graph.Model
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
        fontSprite = Texture(Id(), source = createTextureImage(), hasAlpha = false)
    )

    @Test
    fun textComponent_it_create_text_top_left_aligned() {
        val engine = Engine(gameContext())
        val entity = Entity(engine)
        val component = TextComponent("a", font, gameContext(), lineWith = 2)

        entity.add(Position())
        entity.add(BoundingBoxComponent.default())
        entity.add(
            ModelComponent(
                model = Model(
                    primitives = listOf(
                        com.github.dwursteisen.minigdx.graph.Primitive(
                            texture = Texture(Id(), byteArrayOf(), 0, 0, true)
                        )
                    )
                )
            )
        )
        entity.add(component)

        engine.update(0f)

        val textMeshData = component.generateVertices(1f)
        // 4 vertices for the letter 'a'
        assertEquals(4, textMeshData.vertices.size)
        assertEquals(-1f, textMeshData.vertices.first().position.x)
    }
}
