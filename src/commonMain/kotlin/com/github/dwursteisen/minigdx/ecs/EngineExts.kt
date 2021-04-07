package com.github.dwursteisen.minigdx.ecs

import com.curiouscreature.kotlin.math.ortho
import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.common.Id
import com.dwursteisen.minigdx.scene.api.model.Normal
import com.dwursteisen.minigdx.scene.api.model.Primitive
import com.dwursteisen.minigdx.scene.api.model.UV
import com.dwursteisen.minigdx.scene.api.model.Vertex
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.SpriteComponent
import com.github.dwursteisen.minigdx.ecs.components.UICamera
import com.github.dwursteisen.minigdx.ecs.components.gl.MeshPrimitive
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.dwursteisen.minigdx.scene.api.model.Position as PositionDTO
import com.dwursteisen.minigdx.scene.api.sprite.Sprite as SpriteDTO

fun Engine.createUICamera(gameContext: GameContext): Entity {
    return this.create {
        val width = gameContext.gameScreen.width
        val height = gameContext.gameScreen.height
        add(
            UICamera(
                projection = ortho(
                    l = width * -0.5f,
                    r = width * 0.5f,
                    b = height * -0.5f,
                    t = height * 0.5f,
                    n = 0.01f,
                    f = 1f
                )
            )
        )
        // put the camera in the center of the screen
        add(Position().setWorldTranslation(x = -width * 0.5f, y = -height * 0.5f))
    }
}

fun Engine.createSprite(sprite: SpriteDTO, scene: Scene): Entity = create {
    add(Position())
    add(
        SpriteComponent(
            animations = sprite.animations,
            uvs = sprite.uvs
        )
    )
    add(
        MeshPrimitive(
            id = Id(),
            name = "undefined",
            material = scene.materials.getValue(sprite.materialReference),
            primitive = Primitive(
                id = Id(),
                materialId = sprite.materialReference,
                vertices = listOf(
                    Vertex(PositionDTO(0f, 0f, 0f), Normal(0f, 0f, 0f), uv = UV(0f, 0f)),
                    Vertex(PositionDTO(1f, 0f, 0f), Normal(0f, 0f, 0f), uv = UV(0f, 0f)),
                    Vertex(PositionDTO(0f, 1f, 0f), Normal(0f, 0f, 0f), uv = UV(0f, 0f)),
                    Vertex(PositionDTO(1f, 1f, 0f), Normal(0f, 0f, 0f), uv = UV(0f, 0f))
                ),
                verticesOrder = intArrayOf(
                    0,
                    1,
                    2,
                    2,
                    1,
                    3
                )
            )
        )
    )
}
