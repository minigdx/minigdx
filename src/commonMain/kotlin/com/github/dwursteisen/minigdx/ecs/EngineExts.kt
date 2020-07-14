package com.github.dwursteisen.minigdx.ecs

import com.curiouscreature.kotlin.math.Mat4
import com.curiouscreature.kotlin.math.ortho
import com.curiouscreature.kotlin.math.perspective
import com.dwursteisen.minigdx.scene.api.Scene
import com.dwursteisen.minigdx.scene.api.camera.Camera
import com.dwursteisen.minigdx.scene.api.camera.OrthographicCamera
import com.dwursteisen.minigdx.scene.api.camera.PerspectiveCamera
import com.dwursteisen.minigdx.scene.api.model.Model
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.ecs.components.BoundingBox
import com.github.dwursteisen.minigdx.ecs.components.MeshPrimitive
import com.github.dwursteisen.minigdx.ecs.components.Position
import com.github.dwursteisen.minigdx.ecs.components.SpritePrimitive
import com.github.dwursteisen.minigdx.ecs.components.Text
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.entity.text.Font

@ExperimentalStdlibApi
fun Engine.createFrom(model: Model, scene: Scene, context: GameContext): Entity {
    return this.create {
        if (model.armatureId < 0) {
            model.mesh.primitives.forEach { primitive ->
                add(MeshPrimitive(
                    primitive = primitive,
                    material = scene.materials.values.first { it.id == primitive.materialId }
                ))
            }
            val transformation = Mat4.fromColumnMajor(*model.transformation.matrix)
            add(Position(transformation))
            model.boxes.forEach { add(BoundingBox.from(it)) }
        } else {
            throw IllegalArgumentException("Animated model is not supported yet")
        }
    }
}

fun Engine.createFrom(camera: Camera, context: GameContext): Entity {
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
            val width = context.gl.screen.width / (camera.scale * 2f)
            val height = context.gl.screen.height / (camera.scale * 2f)
            com.github.dwursteisen.minigdx.ecs.components.Camera(
                projection = ortho(
                    l = width * -0.5f,
                    r = width * 0.5f,
                    b = height * -0.5f,
                    t = height * 0.5f,
                    n = camera.near,
                    f = camera.far
                )
            )
        }
        else -> throw IllegalArgumentException("${camera::class} is not supported")
    }

    return this.create {
        add(cameraComponent)
        add(Position(Mat4.fromColumnMajor(*camera.transformation.matrix), way = -1f))
    }
}

fun Engine.createFrom(font: Font, text: String, x: Float, y: Float): Entity {
    return this.create {
        add(Position().translate(x = x, y = y, z = 0f))
        add(
            SpritePrimitive(
            texture = font.fontSprite,
            renderStrategy = SpritePrimitive.TextRenderStrategy
        )
        )
        add(
            Text(
            text = text,
            angelCode = font.angelCode,
            fontSprite = font.fontSprite
        )
        )
    }
}
