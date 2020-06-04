package com.github.dwursteisen.minigdx.render

import com.github.dwursteisen.minigdx.ecs.EntityQuery
import com.github.dwursteisen.minigdx.shaders.FragmentShader
import com.github.dwursteisen.minigdx.shaders.VertexShader

data class RenderOptions(
    val renderName: String,
    var renderOnDisk: Boolean
)

interface Stage

data class RenderStage(
    val vertex: VertexShader,
    val fragment: FragmentShader,
    val query: EntityQuery,
    val renderOnScreen: Boolean = true,
    val renderOption: RenderOptions
) : Stage

data class MixStage(
    val fragment: FragmentShader,
    val query: EntityQuery,
    val renderOnScreen: Boolean = true,
    val renderOption: RenderOptions
) : Stage

class FrameBufferRegistry
