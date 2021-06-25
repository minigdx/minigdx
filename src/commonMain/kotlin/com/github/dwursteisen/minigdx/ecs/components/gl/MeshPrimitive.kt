package com.github.dwursteisen.minigdx.ecs.components.gl

import com.dwursteisen.minigdx.scene.api.common.Id
import com.dwursteisen.minigdx.scene.api.material.Material
import com.dwursteisen.minigdx.scene.api.model.Primitive
import com.github.dwursteisen.minigdx.file.Texture
import com.github.dwursteisen.minigdx.shaders.Buffer
import com.github.dwursteisen.minigdx.shaders.TextureReference

open class MeshPrimitive(
    override var id: Id,
    val name: String,
    /**
     * Model primitive which contains the model data (vertices, ...)
     */
    var primitive: Primitive,
    /**
     * Texture used by the primitive.
     * Can be null if a material is used instead
     */
    val texture: Texture,

    /**
     * Is texture/material has alpha?
     *
     * If the texture or the material has alpha,
     * the rendering needs to be delayed to render
     * it correctly.
     */
    var hasAlpha: Boolean = texture.hasAlpha ?: false,
    var isUVDirty: Boolean = true,
    override var isDirty: Boolean = true,
    // --- Open GL Specific fields --- //
    var verticesBuffer: Buffer? = null,
    var normalsBuffer: Buffer? = null,
    var uvBuffer: Buffer? = null,
    var verticesOrderBuffer: Buffer? = null
) : GLResourceComponent
