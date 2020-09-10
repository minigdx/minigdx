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

    fun createRenderStage(gl: GL, compiler: GLResourceClient): List<RenderStage<*, *>> {
        val stages = mutableListOf<RenderStage<*, *>>()
        stages.add(ClearBufferRenderStage(gl, compiler))
        stages.add(MeshPrimitiveRenderStage(gl, compiler))
        stages.add(AnimatedMeshPrimitiveRenderStage(gl, compiler))
        if (gameContext.options.debug) {
            stages.add(BoundingBoxStage(gl, compiler))
        }
        stages.add(SpritePrimitiveRenderStage(gl, compiler))
        return stages
    }

    fun render(engine: Engine, delta: Seconds) {
        engine.update(delta)
    }

    fun destroy(engine: Engine) = engine.destroy()
}
