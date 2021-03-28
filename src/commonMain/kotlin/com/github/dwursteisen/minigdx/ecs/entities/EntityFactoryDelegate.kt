package com.github.dwursteisen.minigdx.ecs.entities

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
import com.github.dwursteisen.minigdx.api.r
import com.github.dwursteisen.minigdx.api.s
import com.github.dwursteisen.minigdx.api.t
import com.github.dwursteisen.minigdx.api.toMat4
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.AnimatedModel
import com.github.dwursteisen.minigdx.ecs.components.Color
import com.github.dwursteisen.minigdx.ecs.components.LightComponent
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.TextComponent
import com.github.dwursteisen.minigdx.ecs.components.gl.AnimatedMeshPrimitive
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.ecs.components.gl.MeshPrimitive
import com.github.dwursteisen.minigdx.file.Font

class EntityFactoryDelegate : EntityFactory {

    override lateinit var engine: Engine
    override lateinit var gameContext: GameContext

    override fun create(block: (Engine.EntityBuilder) -> Unit): Entity = engine.create(block)

    @ExperimentalStdlibApi
    override fun createFromNode(node: Node, scene: Scene, parent: Entity?): Entity {
        return when (node.type) {
            ObjectType.ARMATURE -> createArmature(node, scene)
            ObjectType.BOX -> createBox(node, scene, parent)
            ObjectType.CAMERA -> createCamera(node, scene)
            ObjectType.LIGHT -> createLight(node, scene)
            ObjectType.MODEL -> createModel(node, scene)
        }
    }

    override fun createBox(node: Node, scene: Scene, parent: Entity?): Entity {
        val box = engine.create {
            val globalTranslation = node.transformation.toMat4()
            named(node.name)
            add(BoundingBox.default())
            add(Position(globalTranslation, globalTranslation, globalTranslation))
        }
        return box.attachTo(parent)
    }

    override fun createText(text: String, font: Font): Entity {
        return engine.create {
            named("text")
            add(Position())
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
                        0,
                        1,
                        2,
                        2,
                        1,
                        3
                    )
                )
            )
            val spritePrimitive = TextComponent(text, font, meshPrimitive)
            add(spritePrimitive)
            add(meshPrimitive)
        }
    }

    override fun createLight(node: Node, scene: Scene): Entity {
        return create {
            val transformation = node.transformation.toMat4()
            it.add(Position(transformation, transformation, transformation))
            it.add(LightComponent(Color(0.6f, 0.6f, 0.6f)))
        }
    }

    @ExperimentalStdlibApi
    override fun createModel(node: Node, scene: Scene): Entity {
        val entity = create {
            it.named(node.name)
            val model = scene.models.getValue(node.reference)
            val position = Position(
                node.transformation.t,
                node.transformation.r,
                node.transformation.s
            )

            it.add(position)

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
        node.children.forEach {
            createFromNode(it, scene, entity)
        }
        return entity
    }

    override fun createUICamera(): Entity {
        return engine.create {}
    }

    @ExperimentalStdlibApi
    override fun createArmature(
        node: Node,
        scene: Scene
    ): Entity {
        val armature = create {
            it.named(node.name)
            // Get the model attached to the armature
            val model = scene.models.getValue(node.children.first { it.type == ObjectType.MODEL }.reference)

            // Create animations
            val allAnimations = scene.animations.getOrElse(node.reference) { emptyList() }
            val animation = allAnimations.lastOrNull()
            val referencePose = scene.armatures.getValue(node.reference)
            if (referencePose.joints.size > 100) {
                throw IllegalArgumentException("Your armature contains more than 100 joints. MiniGDX support only 100 joints")
            }
            val animatedModel = AnimatedModel(
                animation = animation?.frames ?: emptyList(),
                animations = allAnimations.map { it.name to it }.toMap(),
                referencePose = referencePose,
                time = 0f,
                duration = animation?.frames?.maxByOrNull { it.time }?.time ?: 0f
            )
            val animatedMeshPrimitive = model.mesh.primitives.map { primitive ->
                AnimatedMeshPrimitive(
                    primitive = primitive,
                    material = scene.materials.getValue(primitive.materialId)
                )
            }

            // Create components
            it.add(
                Position(
                    node.transformation.t,
                    node.transformation.r,
                    node.transformation.s,
                )
            )
            it.add(animatedModel)
            it.add(animatedMeshPrimitive)
        }

        // Look for the bounding box or create it from the mesh.
        node.children.filter { it.type == ObjectType.BOX }
            .onEach { createFromNode(it, scene, armature) }
            .ifEmpty {
                val model = scene.models.getValue(node.children.first { it.type == ObjectType.MODEL }.reference)
                create {
                    it.named("bounding-box")
                    it.add(Position())
                    it.add(BoundingBox.from(model.mesh))
                }.attachTo(armature)
            }

        return armature
    }

    fun createCamera(
        node: Node,
        scene: Scene
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
        it.add(
            Position(
                node.transformation.t,
                node.transformation.r,
                node.transformation.s
            )
        )
    }
}
