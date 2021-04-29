package com.github.dwursteisen.minigdx.utils

import com.dwursteisen.minigdx.scene.api.common.Id
import com.dwursteisen.minigdx.scene.api.model.Normal
import com.dwursteisen.minigdx.scene.api.model.Position
import com.dwursteisen.minigdx.scene.api.model.Primitive
import com.dwursteisen.minigdx.scene.api.model.UV
import com.dwursteisen.minigdx.scene.api.model.Vertex

object MeshFactoryUtils {

    fun createPlane(): Primitive {
        return Primitive(
            id = Id(),
            materialId = Id.None,
            vertices = listOf(
                Vertex(
                    Position(0f, 0f, 0f),
                    Normal(0f, 0f, 0f),
                    uv = UV(0f, 0f)
                ),
                Vertex(
                    Position(1f, 0f, 0f),
                    Normal(0f, 0f, 0f),
                    uv = UV(1f, 0f)
                ),
                Vertex(
                    Position(0f, 1f, 0f),
                    Normal(0f, 0f, 0f),
                    uv = UV(1f, 1f)
                ),
                Vertex(
                    Position(1f, 1f, 0f),
                    Normal(0f, 0f, 0f),
                    uv = UV(0f, 1f)
                )
            ),
            verticesOrder = intArrayOf(0, 1, 2, 2, 1, 3)
        )
    }
}
