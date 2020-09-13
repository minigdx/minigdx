package com.github.dwursteisen.minigdx.ecs.components.gl

import com.dwursteisen.minigdx.scene.api.common.Id
import com.dwursteisen.minigdx.scene.api.material.Material
import com.github.dwursteisen.minigdx.entity.primitives.Texture
import com.github.dwursteisen.minigdx.render.sprites.RenderStrategy
import com.github.dwursteisen.minigdx.shaders.Buffer
import com.github.dwursteisen.minigdx.shaders.TextureReference

class SpritePrimitive(
    val texture: Texture? = null,
    val material: Material? = null,
    val x: Int = 0,
    val y: Int = 0,
    val width: Int = texture?.width ?: 0,
    val height: Int = texture?.height ?: 0,
    var uvs: FloatArray = floatArrayOf(
        0f, 1f,
        1f, 1f,
        0f, 0f,
        1f, 0f
    ),
    val renderStrategy: RenderStrategy,
    // -- GL specific data -- //
    var isCompiled: Boolean = false,
    var verticesBuffer: Buffer? = null,
    var uvBuffer: Buffer? = null,
    var verticesOrderBuffer: Buffer? = null,
    var textureReference: TextureReference? = null,
    var numberOfIndices: Int = 0,
    override var isDirty: Boolean = true,
    override var id: Id = Id()
) : GLResourceComponent
