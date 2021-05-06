package com.github.dwursteisen.minigdx.ecs.entities

import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.camera.OrthographicCamera
import com.dwursteisen.minigdx.scene.api.camera.PerspectiveCamera
import com.dwursteisen.minigdx.scene.api.common.Id
import com.dwursteisen.minigdx.scene.api.model.Primitive
import com.dwursteisen.minigdx.scene.api.relation.Node
import com.dwursteisen.minigdx.scene.api.relation.ObjectType
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.api.r
import com.github.dwursteisen.minigdx.api.s
import com.github.dwursteisen.minigdx.api.t
import com.github.dwursteisen.minigdx.api.toMat4
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.AnimatedModel
import com.github.dwursteisen.minigdx.ecs.components.Camera
import com.github.dwursteisen.minigdx.ecs.components.Color
import com.github.dwursteisen.minigdx.ecs.components.LightComponent
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.TextComponent
import com.github.dwursteisen.minigdx.ecs.components.gl.AnimatedMeshPrimitive
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.ecs.components.gl.MeshPrimitive
import com.github.dwursteisen.minigdx.ecs.components.text.TextEffect
import com.github.dwursteisen.minigdx.ecs.components.text.WriteText
import com.github.dwursteisen.minigdx.file.Font

class EntityFactoryDelegate : EntityFactory {

    override lateinit var engine: Engine
    override lateinit var gameContext: GameContext

    override fun create(block: (Engine.EntityBuilder) -> Unit): Entity = engine.create(block)

    @ExperimentalStdlibApi
    override fun createFromNode(node: Node, scene: Scene, parent: Entity?): Entity {
        return when (node.type) {
            ObjectType.ARMATURE -> createArmature(node, scene)
            ObjectType.BOX -> createBox(node)
            ObjectType.CAMERA -> createCamera(node, scene)
            ObjectType.LIGHT -> createLight(node, scene)
            ObjectType.MODEL -> createModel(node, scene)
        }.also { entity ->
            node.children.forEach {
                createFromNode(it, scene, entity)
            }
            entity.attachTo(parent)
        }
    }

    override fun createBox(node: Node): Entity {
        val box = engine.create {
            named(node.name)
            add(BoundingBox.default())
            add(Position(node.transformation.t, node.transformation.r, node.transformation.s))
        }
        return box
    }

    override fun createText(text: String, font: Font, node: Node, scene: Scene): Entity {
        return createText(WriteText(text), font, node, scene)
    }

    override fun createText(textEffect: TextEffect, font: Font, node: Node, scene: Scene): Entity {
        val box = createBox(node)
        val meshPrimitive = MeshPrimitive(
            id = Id(),
            name = "undefined",
            material = null,
            texture = font.fontSprite,
            hasAlpha = true,
            primitive = Primitive(
                id = Id(),
                materialId = Id.None,
                vertices = emptyList(),
                verticesOrder = intArrayOf()
            )
        )
        val textComponent = TextComponent(textEffect, font)

        box.add(meshPrimitive)
        box.add(textComponent)

        node.children.forEach {
            createFromNode(it, scene, box)
        }

        return box
    }

    override fun createText(textEffect: TextEffect, font: Font): Entity {
        val box = engine.create {
            named("text-box")
            add(BoundingBox.default())
            add(Position())
        }
        val meshPrimitive = MeshPrimitive(
            id = Id(),
            name = "undefined",
            material = null,
            texture = font.fontSprite,
            hasAlpha = true,
            primitive = Primitive(
                id = Id(),
                materialId = Id.None,
                vertices = emptyList(),
                verticesOrder = intArrayOf()
            )
        )
        val textComponent = TextComponent(textEffect, font)

        box.add(meshPrimitive)
        box.add(textComponent)

        return box
    }

    override fun createLight(node: Node, scene: Scene): Entity {
        return create {
            val pointLight = scene.pointLights.getValue(node.reference)
            val transformation = node.transformation.toMat4()
            it.add(Position(transformation, transformation, transformation))
            it.add(
                LightComponent(
                    Color(
                        pointLight.color.r,
                        pointLight.color.g,
                        pointLight.color.b,
                        pointLight.color.alpha
                    ),
                    pointLight.intensity
                )
            )
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
            it.add(BoundingBox.from(model.mesh))
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
            is PerspectiveCamera -> Camera(
                type = Camera.Type.PERSPECTIVE,
                gameScreen = gameContext.gameScreen,
                fov = camera.fov,
                near = camera.near,
                far = camera.far
            )
            is OrthographicCamera -> {
                Camera(
                    type = Camera.Type.ORTHOGRAPHIC,
                    gameScreen = gameContext.gameScreen,
                    near = camera.near,
                    far = camera.far,
                    scale = camera.scale
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
