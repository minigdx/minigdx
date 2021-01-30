package com.github.dwursteisen.minigdx.ecs.entities

import com.curiouscreature.kotlin.math.Mat4
import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.common.Id
import com.dwursteisen.minigdx.scene.api.model.Normal
import com.dwursteisen.minigdx.scene.api.model.Primitive
import com.dwursteisen.minigdx.scene.api.model.UV
import com.dwursteisen.minigdx.scene.api.model.Vertex
import com.dwursteisen.minigdx.scene.api.relation.Node
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.TextComponent
import com.github.dwursteisen.minigdx.ecs.components.gl.MeshPrimitive
import com.github.dwursteisen.minigdx.file.Font

class EntityFactoryDelegate : EntityFactory {

    override lateinit var engine: Engine
    override lateinit var gameContext: GameContext

    override fun create(block: (Engine.EntityBuilder) -> Unit): Entity = engine.create(block)

    override fun createFromNode(node: Node, scene: Scene, transformation: Mat4): Entity {
        TODO("Not yet implemented")
    }

    override fun createBox(node: Node, scene: Scene, transformation: Mat4): Entity {
        TODO("Not yet implemented")
    }

    override fun createText(text: String, font: Font, transformation: Mat4): Entity {
        return engine.create {
            add(Position(transformation = transformation))
            val meshPrimitive = MeshPrimitive(
                id = Id(),
                name = "undefined",
                material = null,
                texture = font.fontSprite,
                hasAlpha = true,
                primitive = Primitive(
                    id = Id(),
                    materialId = Id.None,
                    vertices = listOf(
                        Vertex(com.dwursteisen.minigdx.scene.api.model.Position(0f, 0f, 0f), Normal(0f, 0f, 0f), uv = UV(0f, 0f)),
                        Vertex(com.dwursteisen.minigdx.scene.api.model.Position(1f, 0f, 0f), Normal(0f, 0f, 0f), uv = UV(1f, 0f)),
                        Vertex(com.dwursteisen.minigdx.scene.api.model.Position(0f, 1f, 0f), Normal(0f, 0f, 0f), uv = UV(1f, 1f)),
                        Vertex(com.dwursteisen.minigdx.scene.api.model.Position(1f, 1f, 0f), Normal(0f, 0f, 0f), uv = UV(0f, 1f))
                    ),
                    verticesOrder = intArrayOf(
                        0, 1, 2,
                        2, 1, 3
                    )
                )
            )
            val spritePrimitive = TextComponent(text, font, meshPrimitive)
            add(spritePrimitive)
        }
    }

    override fun createModel(node: Node, scene: Scene, transformation: Mat4): Entity {
        TODO("Not yet implemented")
    }

    override fun createUICamera(): Entity {
        TODO("Not yet implemented")
    }
}
