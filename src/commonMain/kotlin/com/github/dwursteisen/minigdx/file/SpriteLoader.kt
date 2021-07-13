package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.graph.Sprite

class SpriteLoader : FileLoader<Sprite> {

    private val sceneLoader = GraphSceneLoader()

    override fun load(filename: String, handler: FileHandler): Content<Sprite> {
        return sceneLoader.load(filename, handler).map { graphScene ->
            val spriteFromScene = graphScene.sprites.values.first()
            val material = graphScene.scene.materials[spriteFromScene.materialReference]!!
            val spriteSheet = Texture(
                spriteFromScene.materialReference,
                material.data,
                material.width,
                material.height,
                material.hasAlpha
            )
            val animations = spriteFromScene.animations
            val uvs = spriteFromScene.uvs

            val sprite = Sprite(
                spriteSheet = spriteSheet,
                animations = animations,
                uvs = uvs
            )
            handler.gameContext.assetsManager.add(spriteSheet)

            sprite
        }
    }
}
