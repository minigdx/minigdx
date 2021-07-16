package com.github.dwursteisen.minigdx.ecs.entities

import com.curiouscreature.kotlin.math.Mat4
import com.dwursteisen.minigdx.scene.api.model.UV
import com.dwursteisen.minigdx.scene.api.relation.ObjectType
import com.dwursteisen.minigdx.scene.api.sprite.Frame
import com.dwursteisen.minigdx.scene.api.sprite.SpriteAnimation
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.AnimatedComponent
import com.github.dwursteisen.minigdx.ecs.components.BoundingBoxComponent
import com.github.dwursteisen.minigdx.ecs.components.CameraComponent
import com.github.dwursteisen.minigdx.ecs.components.LightComponent
import com.github.dwursteisen.minigdx.ecs.components.ModelComponent
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.SpriteComponent
import com.github.dwursteisen.minigdx.ecs.components.TextComponent
import com.github.dwursteisen.minigdx.ecs.components.text.TextEffect
import com.github.dwursteisen.minigdx.ecs.components.text.WriteText
import com.github.dwursteisen.minigdx.file.Font
import com.github.dwursteisen.minigdx.file.Texture
import com.github.dwursteisen.minigdx.graph.GraphNode
import com.github.dwursteisen.minigdx.graph.Model
import com.github.dwursteisen.minigdx.graph.Primitive
import com.github.dwursteisen.minigdx.graph.Sprite

class EntityFactoryDelegate : EntityFactory {

    override lateinit var engine: Engine
    override lateinit var gameContext: GameContext

    override fun create(block: (Engine.EntityBuilder) -> Unit): Entity = engine.create(block)

    override fun createText(textEffect: TextEffect, font: Font): Entity {
        val entity = engine.create {
            named("text node")
            add(
                TextComponent(
                    text = textEffect,
                    font = font,
                    gameContext = gameContext
                )
            )
            add(
                ModelComponent(
                    model = Model(
                        primitives = listOf(
                            Primitive(
                                texture = font.fontSprite
                            )
                        )
                    )
                )
            )
            add(BoundingBoxComponent.default())
            add(Position())
        }
        return entity
    }

    override fun createFromNode(node: GraphNode, parent: Entity?): Entity {
        return when (node.type) {
            ObjectType.ARMATURE -> createAnimatedModel(node)
            ObjectType.BOX -> createBox(node)
            ObjectType.CAMERA -> createCamera(node)
            ObjectType.LIGHT -> createLight(node)
            ObjectType.MODEL -> createModel(node)
        }.also { entity ->
            node.children.forEach {
                createFromNode(it, entity)
            }
            entity.attachTo(parent)
        }
    }

    override fun createBox(node: GraphNode): Entity = engine.create {
        named(node.name)
        add(BoundingBoxComponent.default())
        add(
            Position(
                translation = node.translation,
                rotation = node.rotation,
                scale = node.scale
            )
        )
    }

    override fun createText(text: String, font: Font, node: GraphNode): Entity = createText(WriteText(text), font, node)

    override fun createText(text: TextEffect, font: Font, node: GraphNode): Entity {
        val entity = engine.create {
            named(node.name)
            add(
                TextComponent(
                    text = text,
                    font = font,
                    gameContext = gameContext
                )
            )
            add(
                ModelComponent(
                    model = Model(
                        primitives = listOf(
                            Primitive(
                                texture = font.fontSprite
                            )
                        )
                    )
                )
            )
            add(BoundingBoxComponent.default())
            add(
                Position(
                    translation = node.translation,
                    rotation = node.rotation,
                    scale = node.scale
                )
            )
        }
        node.children.forEach {
            createFromNode(it, entity)
        }
        return entity
    }

    override fun createModel(node: GraphNode): Entity = engine.create {
        val model = node.model
        named(node.name)
        add(
            ModelComponent(
                model = model
            )
        )
        add(
            Position(
                translation = node.translation,
                rotation = node.rotation,
                scale = node.scale
            )
        )
        add(BoundingBoxComponent.from(model))
    }

    override fun createCamera(node: GraphNode): Entity = engine.create {
        val camera = node.camera
        named(node.name)
        add(
            CameraComponent(
                gameContext.gameScreen,
                type = camera.type,
                far = camera.far,
                near = camera.near,
                fov = camera.fov,
                scale = camera.scale
            )
        )
        add(
            Position(
                translation = node.translation,
                rotation = node.rotation,
                scale = node.scale
            )
        )
    }

    override fun createAnimatedModel(node: GraphNode): Entity = engine.create {
        val animatedModel = node.animatedModel
        named(node.name)
        add(
            AnimatedComponent(
                animatedModel = animatedModel
            )
        )
        add(
            Position(
                translation = node.translation,
                rotation = node.rotation,
                scale = node.scale
            )
        )
    }

    override fun createLight(node: GraphNode): Entity = engine.create {
        val light = node.light
        named(node.name)
        add(
            LightComponent(
                color = light.color.copy(),
                intensity = light.intensity
            )
        )
        add(
            Position(
                translation = node.translation,
                rotation = node.rotation,
                scale = node.scale
            )
        )
    }

    override fun createSprite(sprite: Sprite, position: Mat4): Entity = engine.create {
        add(Position(position, position, position))
        add(
            SpriteComponent(
                animations = sprite.animations,
                uvs = sprite.uvs
            )
        )
        add(BoundingBoxComponent.default())
        val quad = Model(
            primitives = listOf(
                Primitive(
                    texture = sprite.spriteSheet,
                    vertices = floatArrayOf(
                        -1f, -1f, 0f,
                        1f, -1f, 0f,
                        -1f, 1f, 0f,
                        1f, 1f, 0f
                    ),
                    normals = floatArrayOf(
                        0f, 0f, 0f,
                        0f, 0f, 0f,
                        0f, 0f, 0f,
                        0f, 0f, 0f
                    ),
                    uvs = floatArrayOf(
                        0f,
                        0f,
                        0f,
                        0f,
                        0f,
                        0f,
                        0f,
                        0f
                    ),
                    verticesOrder = shortArrayOf(
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
        add(
            ModelComponent(
                model = quad
            )
        )

        gameContext.assetsManager.add(quad)
    }

    override fun createSprite(
        texture: Texture,
        spriteWidth: Int,
        spriteHeight: Int,
        position: Mat4,
        animations: AnimationBuilder.() -> Unit
    ): Entity = engine.create {

        fun getFrame(frameIndex: Int): Pair<Int, Int> {
            val framePerLine = (texture.width / spriteWidth)
            val frameX = frameIndex % framePerLine
            val frameY = frameIndex / framePerLine
            return (frameX * spriteWidth) to (frameY * spriteHeight)
        }

        val animationBuilder = AnimationBuilder()
        animationBuilder.animations()
        val animationsMap = animationBuilder.animations.map { (name, framesDuration) ->
            SpriteAnimation(
                name = name,
                // Convert milliseconds to seconds
                duration = framesDuration.values.sum() / 1000f,
                frames = framesDuration.entries
                    .toList()
                    .sortedBy { (index, _) -> index }
                    .map { (frame, duration) ->
                        Frame(
                            duration = duration / 1000f,
                            uvIndex = frame * 4
                        )
                    }
            )
        }.map { animation ->
            animation.name to animation
        }.toMap()

        val framePerLine = (texture.width / spriteWidth)
        val framePerColumn = (texture.height / spriteHeight)
        val nbFrame = framePerLine * framePerColumn
        val uvs = (0..nbFrame).flatMap { frame ->
            val (startX, startY) = getFrame(frame)
            val endX = startX + spriteWidth
            val endY = startY + spriteHeight
            val startUVX = (startX / texture.width.toFloat())
            val startUVY = (startY / texture.height.toFloat())
            val endUVX = (endX / texture.width.toFloat())
            val endUVY = (endY / texture.height.toFloat())
            val a = UV(
                x = startUVX,
                y = endUVY
            )
            val b = UV(
                x = startUVX,
                y = startUVY
            )
            val c = UV(
                x = endUVX,
                y = startUVY
            )
            val d = UV(
                x = endUVX,
                y = endUVY
            )
            listOf(a, b, c, d)
        }
        add(Position(position, position, position))
        add(
            SpriteComponent(
                animations = animationsMap,
                uvs = uvs
            )
        )
        add(BoundingBoxComponent.default())
        val quad = Model(
            primitives = listOf(
                Primitive(
                    texture = texture,
                    vertices = floatArrayOf(
                        -1f, -1f, 0f,
                        1f, -1f, 0f,
                        -1f, 1f, 0f,
                        1f, 1f, 0f
                    ),
                    normals = floatArrayOf(
                        0f, 0f, 0f,
                        0f, 0f, 0f,
                        0f, 0f, 0f,
                        0f, 0f, 0f
                    ),
                    uvs = floatArrayOf(
                        0f,
                        0f,
                        0f,
                        0f,
                        0f,
                        0f,
                        0f,
                        0f
                    ),
                    verticesOrder = shortArrayOf(
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
        add(
            ModelComponent(
                model = quad
            )
        )

        gameContext.assetsManager.add(quad)
    }
}
