package com.github.dwursteisen.minigdx.ecs.entities

import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.ortho
import com.curiouscreature.kotlin.math.perspective
import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.camera.OrthographicCamera
import com.dwursteisen.minigdx.scene.api.camera.PerspectiveCamera
import com.dwursteisen.minigdx.scene.api.common.Id
import com.dwursteisen.minigdx.scene.api.model.Normal
import com.dwursteisen.minigdx.scene.api.model.Primitive
import com.dwursteisen.minigdx.scene.api.model.UV
import com.dwursteisen.minigdx.scene.api.model.Vertex
import com.dwursteisen.minigdx.scene.api.relation.Node
import com.dwursteisen.minigdx.scene.api.relation.ObjectType
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.api.toMat4
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.TextComponent
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.ecs.components.gl.MeshPrimitive
import com.github.dwursteisen.minigdx.file.Font

class EntityFactoryDelegate : EntityFactory {

    override lateinit var engine: Engine
    override lateinit var gameContext: GameContext

    override fun create(block: (Engine.EntityBuilder) -> Unit): Entity = engine.create(block)

    @ExperimentalStdlibApi
    override fun createFromNode(node: Node, scene: Scene, transformation: Mat4): Entity {
        return when (node.type) {
            ObjectType.ARMATURE -> TODO()
            ObjectType.BOX -> createBox(node, scene, transformation)
            ObjectType.CAMERA -> createCamera(node, scene, transformation)
            ObjectType.LIGHT -> TODO()
            ObjectType.MODEL -> createModel(node, scene, transformation)
        }
    }

    override fun createBox(node: Node, scene: Scene, transformation: Mat4): Entity {
        return engine.create {}
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
                        Vertex(
                            com.dwursteisen.minigdx.scene.api.model.Position(0f, 0f, 0f),
                            Normal(0f, 0f, 0f),
                            uv = UV(0f, 0f)
                        ),
                        Vertex(
                            com.dwursteisen.minigdx.scene.api.model.Position(1f, 0f, 0f),
                            Normal(0f, 0f, 0f),
                            uv = UV(1f, 0f)
                        ),
                        Vertex(
                            com.dwursteisen.minigdx.scene.api.model.Position(0f, 1f, 0f),
                            Normal(0f, 0f, 0f),
                            uv = UV(1f, 1f)
                        ),
                        Vertex(
                            com.dwursteisen.minigdx.scene.api.model.Position(1f, 1f, 0f),
                            Normal(0f, 0f, 0f),
                            uv = UV(0f, 1f)
                        )
                    ),
                    verticesOrder = intArrayOf(
                        0, 1, 2,
                        2, 1, 3
                    )
                )
            )
            val spritePrimitive = TextComponent(text, font, meshPrimitive)
            add(spritePrimitive)
            add(meshPrimitive)
        }
    }

    @ExperimentalStdlibApi
    override fun createModel(node: Node, scene: Scene, transformation: Mat4): Entity {
        return create {
            val model = scene.models.getValue(node.reference)
            val boxes = node.children.filter { it.type == ObjectType.BOX }
                .map { BoundingBox.from(it.transformation.toMat4()) }
                .ifEmpty { listOf(BoundingBox.from(model.mesh)) }

            it.add(boxes)
            it.add(Position(transformation))

            val primitives = model.mesh.primitives.map { primitive ->
                val material =
                    scene.materials[primitive.materialId] ?: throw IllegalStateException(
                        "Model ${model.name} doesn't have any material assigned."
                    )
                MeshPrimitive(
                    id = primitive.id,
                    primitive = primitive,
                    material = material,
                    name = node.name
                )
            }
            it.add(primitives)
        }
    }

    override fun createUICamera(): Entity {
        return engine.create {}
    }

    fun createCamera(
        node: Node,
        scene: Scene,
        transformation: Mat4
    ): Entity = create {
        val camera = scene.perspectiveCameras[node.reference] ?: scene.orthographicCameras.getValue(node.reference)
        val cameraComponent = when (camera) {
            is PerspectiveCamera -> com.github.dwursteisen.minigdx.ecs.components.Camera(
                projection = perspective(
                    fov = camera.fov,
                    aspect = gameContext.gameScreen.ratio,
                    near = camera.near,
                    far = camera.far
                )
            )
            is OrthographicCamera -> {
                val (w, h) = if (gameContext.gameScreen.width >= gameContext.gameScreen.height) {
                    // 1 / GameScreen.ratio
                    1f to (gameContext.gameScreen.height / gameContext.gameScreen.width.toFloat())
                } else {
                    // GameScreen.ratio
                    gameContext.gameScreen.width / gameContext.gameScreen.height.toFloat() to 1f
                }
                com.github.dwursteisen.minigdx.ecs.components.Camera(
                    projection = ortho(
                        l = -camera.scale * 0.5f * w,
                        r = camera.scale * 0.5f * w,
                        b = -camera.scale * 0.5f * h,
                        t = camera.scale * 0.5f * h,
                        n = camera.near,
                        f = camera.far
                    )
                )
            }
            else -> throw IllegalArgumentException("${camera::class} is not supported")
        }

        it.add(cameraComponent)
        it.add(Position(transformation, way = -1f))
    }
}
