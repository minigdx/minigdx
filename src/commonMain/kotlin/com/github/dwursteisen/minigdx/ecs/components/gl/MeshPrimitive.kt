package com.github.dwursteisen.minigdx.ecs.components.gl

import com.dwursteisen.minigdx.scene.api.material.Material
import com.dwursteisen.minigdx.scene.api.model.Primitive
import com.github.dwursteisen.minigdx.shaders.Buffer
import com.github.dwursteisen.minigdx.shaders.TextureReference

open class MeshPrimitive(
    var isCompiled: Boolean = false,
    val primitive: Primitive,
    val material: Material,
    var verticesBuffer: Buffer? = null,
    var uvBuffer: Buffer? = null,
    var verticesOrderBuffer: Buffer? = null,
    var textureReference: TextureReference? = null,
    override var isDirty: Boolean = true
) : GLResourceComponent