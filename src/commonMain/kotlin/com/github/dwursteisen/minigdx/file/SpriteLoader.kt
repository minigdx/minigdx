package com.github.dwursteisen.minigdx.file

import com.github.dwursteisen.minigdx.graph.Sprite

class SpriteLoader : FileLoader<Sprite> {

    private val sceneLoader = GraphSceneLoader()

    override fun load(filename: String, handler: FileHandler): Content<Sprite> {
        return sceneLoader.load(filename, handler).map { graphScene ->
            val spriteFromScene = graphScene.sprites.values.first()
            val material = graphScene.scene.materials[spriteFromScene.materialReference]!!

            val spriteSheet = Texture(
                id = spriteFromScene.materialReference,
                textureData = byteArrayOf(
                    0xFF.toByte(), 0x00.toByte(), 0x00.toByte(), 0xFF.toByte(),
                    0x00.toByte(), 0xFF.toByte(), 0x00.toByte(), 0xFF.toByte(),
                    0x00.toByte(), 0x00.toByte(), 0xFF.toByte(), 0xFF.toByte(),
                    0xFF.toByte(), 0xFF.toByte(), 0x00.toByte(), 0xFF.toByte()
                ),
                width = 2,
                height = 2,
                hasAlpha = material.hasAlpha
            )
            handler.decodeTextureImage(filename, material.data).onLoaded { loadedTextureImage ->
                // The final texture is loaded.
                // Load this texture instead of the default one.
                spriteSheet.textureImage = loadedTextureImage
                spriteSheet.height = loadedTextureImage.height
                spriteSheet.width = loadedTextureImage.width
                handler.gameContext.assetsManager.add(spriteSheet)
            }

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
