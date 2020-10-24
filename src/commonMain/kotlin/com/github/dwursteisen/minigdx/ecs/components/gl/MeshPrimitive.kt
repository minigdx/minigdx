package com.github.dwursteisen.minigdx.ecs.components.gl

import com.curiouscreature.kotlin.math.Mat4
import com.dwursteisen.minigdx.scene.api.common.Id
import com.dwursteisen.minigdx.scene.api.material.Material
import com.dwursteisen.minigdx.scene.api.model.Primitive
import com.github.dwursteisen.minigdx.shaders.Buffer
import com.github.dwursteisen.minigdx.shaders.TextureReference

open class MeshPrimitive(
    var isCompiled: Boolean = false,
    var primitive: Primitive,
    val material: Material,
    var verticesBuffer: Buffer? = null,
    var uvBuffer: Buffer? = null,
    var verticesOrderBuffer: Buffer? = null,
    var textureReference: TextureReference? = null,
    var transformation: Mat4? = null,
    var isUVDirty: Boolean = true,
    override var isDirty: Boolean = true,
    override var id: Id,
    val name: String
) : GLResourceComponent
