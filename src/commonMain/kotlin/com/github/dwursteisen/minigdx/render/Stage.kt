package com.github.dwursteisen.minigdx.render

import com.github.dwursteisen.minigdx.ecs.EntityQuery
import com.github.dwursteisen.minigdx.shaders.FragmentShader
import com.github.dwursteisen.minigdx.shaders.VertexShader

interface Stage

interface Interceptor {

    fun intercept(stage: Stage)
}

data class RenderStage(
    val name: String,
    val vertex: VertexShader,
    val fragment: FragmentShader,
    val query: EntityQuery,
    val publisher: Interceptor? = null
) : Stage

data class MixStage(
    val name: String,
    val reference: Stage,
    val fragment: FragmentShader,
    val query: EntityQuery,
    val publisher: Interceptor? = null
) : Stage

class RenderManager
