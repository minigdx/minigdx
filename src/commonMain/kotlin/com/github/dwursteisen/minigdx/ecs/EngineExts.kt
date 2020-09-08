package com.github.dwursteisen.minigdx.ecs

import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.ortho
import com.curiouscreature.kotlin.math.perspective
import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.camera.OrthographicCamera
import com.dwursteisen.minigdx.scene.api.camera.PerspectiveCamera
import com.dwursteisen.minigdx.scene.api.relation.Node
import com.dwursteisen.minigdx.scene.api.relation.ObjectType
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.api.toMat4
import com.github.dwursteisen.minigdx.ecs.components.AnimatedModel
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.Text
import com.github.dwursteisen.minigdx.ecs.components.UICamera
import com.github.dwursteisen.minigdx.ecs.components.gl.AnimatedMeshPrimitive
import com.github.dwursteisen.minigdx.ecs.components.gl.BoundingBox
import com.github.dwursteisen.minigdx.ecs.components.gl.MeshPrimitive
import com.github.dwursteisen.minigdx.ecs.components.gl.SpritePrimitive
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.entity.text.Font
import com.github.dwursteisen.minigdx.render.sprites.TextRenderStrategy

@ExperimentalStdlibApi
fun Engine.createFromNode(node: Node, gameContext: GameContext, scene: Scene, transformation: Mat4 = node.transformation.toMat4()): Entity {
    return when (node.type) {
        ObjectType.ARMATURE -> createArmature(node, scene, transformation)
        ObjectType.BOX -> createBox(node, scene, transformation)
        ObjectType.CAMERA -> createCamera(node, gameContext, scene, transformation)
        ObjectType.LIGHT -> TODO()
        ObjectType.MODEL -> createModel(node, scene, transformation)
    }
}

@ExperimentalStdlibApi
fun Engine.createBox(
    node: Node,
    scene: Scene,
    transformation: Mat4
): Entity = create {
    val box = scene.boxes.getValue(node.reference)
    add(BoundingBox.from(box, node.transformation.toMat4()))
    add(Position(transformation))
}

@ExperimentalStdlibApi
fun Engine.createArmature(
    node: Node,
    scene: Scene,
    transformation: Mat4
): Entity = create {
    val model = scene.models.getValue(node.children.first { it.type == ObjectType.MODEL }.reference)
    val boxes = node.children.filter { it.type == ObjectType.BOX }
        .map { BoundingBox.from(scene.boxes.getValue(it.reference), it.transformation.toMat4()) }
        .ifEmpty { listOf(BoundingBox.from(model.mesh)) }
    val allAnimations = scene.animations.getValue(node.reference)
    val animation = allAnimations.last()
    val animatedModel = AnimatedModel(
        animation = animation.frames,
        referencePose = scene.armatures.getValue(node.reference),
        time = 0f,
        duration = animation.frames.maxBy { it.time }?.time ?: 0f
    )
    val animatedMeshPrimitive = model.mesh.primitives.map { primitive ->
        AnimatedMeshPrimitive(
            primitive = primitive,
            material = scene.materials.getValue(primitive.materialId)
        )
    }
    add(boxes)
    add(Position(transformation))
    add(animatedModel)
    add(animatedMeshPrimitive)
}

@ExperimentalStdlibApi
fun Engine.createModel(
    node: Node,
    scene: Scene,
    transformation: Mat4
): Entity = create {
    val model = scene.models.getValue(node.reference)
    val boxes = node.children.filter { it.type == ObjectType.BOX }
        .map { BoundingBox.from(scene.boxes.getValue(it.reference), it.transformation.toMat4()) }
        .ifEmpty { listOf(BoundingBox.from(model.mesh)) }

    add(boxes)
    add(Position(transformation))

    val primitives = model.mesh.primitives.map { primitive ->
        val material =
            scene.materials[primitive.materialId] ?: throw IllegalStateException(
                "Model ${model.name} doesn't have any material assigned."
            )
        MeshPrimitive(
            id = primitive.id,
            primitive = primitive,
            material = material
        )
    }
    add(primitives)
}

fun Engine.createCamera(
    node: Node,
    context: GameContext,
    scene: Scene,
    transformation: Mat4
): Entity = create {
    val camera = scene.perspectiveCameras[node.reference] ?: scene.orthographicCameras.getValue(node.reference)
    val cameraComponent = when (camera) {
        is PerspectiveCamera -> com.github.dwursteisen.minigdx.ecs.components.Camera(
            projection = perspective(
                fov = camera.fov,
                aspect = context.ratio,
                near = camera.near,
                far = camera.far
            )
        )
        is OrthographicCamera -> {
            val (w, h) = if (context.gl.screen.width >= context.gl.screen.height) {
                1f to (context.gl.screen.height / context.gl.screen.width.toFloat())
            } else {
                context.gl.screen.width / context.gl.screen.height.toFloat() to 1f
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

    add(cameraComponent)
    add(Position(transformation, way = -1f))
}

fun Engine.createUICamera(gameContext: GameContext): Entity {
    return this.create {
        val width = gameContext.gl.screen.width
        val height = gameContext.gl.screen.height
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
        add(Position(way = -1f).translate(x = -width * 0.5f, y = -height * 0.5f))
    }
}

fun Engine.createModel(font: Font, text: String, x: Float, y: Float): Entity {
    return this.create {
        add(Position().translate(x = x, y = y, z = 0f))
        val spritePrimitive = SpritePrimitive(
            texture = font.fontSprite,
            renderStrategy = TextRenderStrategy
        )
        add(spritePrimitive)
        add(
            Text(
                text = text,
                font = font
            )
        )
    }
}
