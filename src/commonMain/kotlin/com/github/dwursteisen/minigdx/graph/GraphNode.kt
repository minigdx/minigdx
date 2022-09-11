package com.github.dwursteisen.minigdx.graph

import com.curiouscreature.kotlin.math.Mat4
import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.camera.OrthographicCamera
import com.dwursteisen.minigdx.scene.api.camera.PerspectiveCamera
import com.dwursteisen.minigdx.scene.api.relation.Node
import com.dwursteisen.minigdx.scene.api.relation.ObjectType
import com.github.dwursteisen.minigdx.api.r
import com.github.dwursteisen.minigdx.api.s
import com.github.dwursteisen.minigdx.api.t
import com.github.dwursteisen.minigdx.api.toMat4
import com.github.dwursteisen.minigdx.ecs.components.CameraComponent
import com.github.dwursteisen.minigdx.ecs.components.Color
import com.github.dwursteisen.minigdx.file.Texture

private fun <T> cache(block: () -> T) = lazy(LazyThreadSafetyMode.NONE, block)

/**
 * Node of a Graph scene.
 */
class GraphNode(
    private val parent: GraphScene,
    private val scene: Scene,
    private val node: Node
) {

    /**
     * Name of the node.
     */
    val name = node.name

    /**
     * Children of the node.
     */
    val children = node.children.map {
        GraphNode(parent, scene, it)
    }

    val customProperties = node.customProperties

    /**
     * Get all children's node of [type].
     */
    fun getAll(type: ObjectType): List<GraphNode> {
        val byTypes = children.flatMap { it.getAll(type) }
        return if (type == node.type) {
            listOf(this) + byTypes
        } else {
            byTypes
        }
    }

    /**
     * Type of the node.
     */
    val type = node.type

    /**
     * Get the Empty information from the node.
     */
    val empty: Empty by cache {
        Empty
    }

    /**
     * Get the animated model from the node.
     */
    val animatedModel: AnimatedModel by cache {
        val models = children.filter { it.type == ObjectType.MODEL }
            .map { it.model }

        // Get animations
        val allAnimations = scene.animations.getOrElse(node.reference) { emptyList() }

        // Get the T-pose
        val referencePose = scene.armatures.getValue(node.reference)
        if (referencePose.joints.size > 50) {
            throw IllegalArgumentException("Your armature contains more than 50 joints. MiniGDX support only 50 joints")
        }

        AnimatedModel(
            models = models,
            animations = allAnimations.map { it.name to it }.toMap(),
            referencePose = referencePose,
        )
    }

    /**
     * Get the light information from the node.
     */
    val light: LightDescription by cache {
        val pointLight = scene.pointLights.getValue(node.reference)
        LightDescription(
            Color(
                pointLight.color.r,
                pointLight.color.g,
                pointLight.color.b,
                pointLight.color.alpha
            ),
            pointLight.intensity
        )
    }

    /**
     * Get the model information from the node.
     */
    val model: Model by cache {
        if (node.type != ObjectType.MODEL) {
            throw IllegalStateException("The current GraphNode is not referencing a Model but a ${node.type} instead.")
        }
        val id = node.reference
        val meshModel = scene.models.getValue(id)

        val primitives = meshModel.mesh.primitives.map { primitive ->
            val material = scene.materials[primitive.materialId] ?: throw IllegalStateException(
                "Model ${meshModel.name} doesn't have any material assigned."
            )

            val texture = parent.textureCache.getOrPut(material.id) {
                Texture(
                    id = material.id,
                    textureData = material.data,
                    width = material.width,
                    height = material.height,
                    hasAlpha = material.hasAlpha
                ).also { parent.assetsManager.add(it) }
            }

            // TODO: The loop can be optimized in one pass intead of multiple pass.
            Primitive(
                vertices = primitive.vertices.flatMap { v ->
                    listOf(v.position.x, v.position.y, v.position.z)
                }.toFloatArray(),
                normals = primitive.vertices.flatMap { v ->
                    listOf(v.normal.x, v.normal.y, v.normal.z)
                }.toFloatArray(),
                uvs = primitive.vertices.flatMap { v ->
                    listOf(v.uv.x, v.uv.y)
                }.toFloatArray(),
                verticesOrder = primitive.verticesOrder.map { it.toShort() }.toShortArray(),
                texture = texture,
                weights = primitive.vertices.flatMap { v ->
                    v.influences.map { i -> i.weight }
                }.toFloatArray(),
                joints = primitive.vertices.flatMap { v ->
                    v.influences.map { i -> i.jointId.toFloat() }
                }.toFloatArray()
            )
        }

        Model(primitives).also { parent.assetsManager.add(it) }
    }

    /**
     * Get the camera information from the node.
     */
    val camera: CameraNode by cache {
        val camera = scene.perspectiveCameras[node.reference] ?: scene.orthographicCameras.getValue(node.reference)
        when (camera) {
            is PerspectiveCamera -> CameraNode(
                type = CameraComponent.Type.PERSPECTIVE,
                fov = camera.fov,
                near = camera.near,
                far = camera.far
            )
            is OrthographicCamera -> {
                CameraNode(
                    type = CameraComponent.Type.ORTHOGRAPHIC,
                    near = camera.near,
                    far = camera.far,
                    scale = camera.scale
                )
            }
            else -> throw IllegalArgumentException("${camera::class} is not supported")
        }
    }

    /**
     * Combined transformation of the node.
     */
    val combinedTransformation: Mat4 = node.transformation.toMat4()

    val translation: Mat4 = node.transformation.t
    val rotation: Mat4 = node.transformation.r
    val scale: Mat4 = node.transformation.s
}
