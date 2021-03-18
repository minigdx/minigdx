package com.github.dwursteisen.minigdx.ecs.components.gl

import com.dwursteisen.minigdx.scene.api.common.Id
import com.dwursteisen.minigdx.scene.api.material.Material
import com.dwursteisen.minigdx.scene.api.model.Primitive
import com.github.dwursteisen.minigdx.shaders.Buffer
import com.github.dwursteisen.minigdx.shaders.TextureReference

class AnimatedMeshPrimitive(
    val primitive: Primitive,
    val material: Material,
    var verticesBuffer: Buffer? = null,
    var normalsBuffer: Buffer? = null,
    var uvBuffer: Buffer? = null,
    var verticesOrderBuffer: Buffer? = null,
    var weightBuffer: Buffer? = null,
    var jointBuffer: Buffer? = null,
    var textureReference: TextureReference? = null,
    override var isDirty: Boolean = true,
    override var id: Id = Id()
) : GLResourceComponent
