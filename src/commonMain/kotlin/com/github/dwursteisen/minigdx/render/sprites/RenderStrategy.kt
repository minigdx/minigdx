package com.github.dwursteisen.minigdx.render.sprites

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.ecs.entities.Entity

interface RenderStrategy {

    fun render(gl: GL, entity: Entity)
}
