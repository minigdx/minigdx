package com.github.dwursteisen.minigdx.game

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.systems.ArmatureUpdateSystem
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.graphics.GLResourceClient
import com.github.dwursteisen.minigdx.render.AnimatedMeshPrimitiveRenderStage
import com.github.dwursteisen.minigdx.render.BoundingBoxStage
import com.github.dwursteisen.minigdx.render.ClearBufferRenderStage
import com.github.dwursteisen.minigdx.render.MeshPrimitiveRenderStage
import com.github.dwursteisen.minigdx.render.RenderStage
import com.github.dwursteisen.minigdx.render.SpritePrimitiveRenderStage

interface Screen {

    val gameContext: GameContext

    fun createEntities(engine: Engine)

    fun createSystems(engine: Engine): List<System> = listOf(
        ArmatureUpdateSystem()
    )

    fun createRenderStage(gl: GL, compiler: GLResourceClient): List<RenderStage<*, *>> = listOf(
        ClearBufferRenderStage(gl, compiler),
        MeshPrimitiveRenderStage(gl, compiler),
        AnimatedMeshPrimitiveRenderStage(gl, compiler),
        BoundingBoxStage(gl, compiler),
        // 2D space rendering
        SpritePrimitiveRenderStage(gl, compiler)
    )

    fun render(engine: Engine, delta: Seconds) {
        engine.update(delta)
    }

    fun destroy(engine: Engine) = engine.destroy()
}
