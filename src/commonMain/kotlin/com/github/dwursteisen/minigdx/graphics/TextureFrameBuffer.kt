package com.github.dwursteisen.minigdx.graphics

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Resolution
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.render.QuadRenderStage
import com.github.dwursteisen.minigdx.render.RenderStage
import com.github.dwursteisen.minigdx.shaders.ShaderProgram
import com.github.dwursteisen.minigdx.shaders.fragment.FragmentShader

abstract class TextureFrameBuffer<T : FragmentShader>(
    name: String,
    gameContext: GameContext,
    resolution: Resolution,
    val fragmentShader: T,
    dependencies: List<FrameBuffer> = emptyList(),
    renderOnScreen: Boolean = false,
    withDepthBuffer: Boolean = false,
) : FrameBuffer(
    name,
    gameContext,
    resolution,
    mutableListOf(),
    dependencies,
    renderOnScreen,
    withDepthBuffer
) {

    val stage = QuadRenderStage(gameContext, fragmentShader, this)

    init {
        // Add the Texture Fragment Buffer into the stage
        @Suppress("UNCHECKED_CAST")
        val mutableDependencies = stages as MutableList<RenderStage<*, *>>
        mutableDependencies.add(stage)
    }

    abstract fun updateFragmentShader(delta: Seconds)

    val program: ShaderProgram by lazy(LazyThreadSafetyMode.NONE) { stage.program }
}
