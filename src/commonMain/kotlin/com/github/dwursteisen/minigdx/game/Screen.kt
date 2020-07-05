package com.github.dwursteisen.minigdx.game

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.systems.ArmatureUpdateSystem
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.gl
import com.github.dwursteisen.minigdx.render.AnimatedMeshPrimitiveRenderStage
import com.github.dwursteisen.minigdx.render.MeshPrimitiveRenderStage
import com.github.dwursteisen.minigdx.render.RenderStage

interface Screen {

    fun createEntities(engine: Engine)

    fun createSystems(): List<System> = listOf(
        ArmatureUpdateSystem()
    )

    fun createRenderStage(): List<RenderStage<*, *>> = listOf(
        MeshPrimitiveRenderStage(),
        AnimatedMeshPrimitiveRenderStage()
    )

    fun render(engine: Engine, delta: Seconds) {
        // FIXME: should not be here
        gl.clearColor(0f, 0f, 0f, 1f)
        gl.clearDepth(1.0)
        gl.enable(GL.DEPTH_TEST)
        gl.depthFunc(GL.LEQUAL)
        gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)

        engine.update(delta)
    }

    fun destroy(engine: Engine) = engine.destroy()
}
