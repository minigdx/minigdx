package com.github.dwursteisen.minigdx.game

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.System
import com.github.dwursteisen.minigdx.render.RenderStage

interface Screen {

    fun createEntities(engine: Engine)

    fun createSystems(): List<System>

    fun createRenderStage(): List<RenderStage<*, *>> = emptyList()

    fun render(engine: Engine, delta: Seconds) = engine.update(delta)

    fun destroy(engine: Engine) = engine.destroy()
}
