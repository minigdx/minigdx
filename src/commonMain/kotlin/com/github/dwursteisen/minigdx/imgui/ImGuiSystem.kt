package com.github.dwursteisen.minigdx.imgui

import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.minigdx.imgui.WidgetBuilder

abstract class ImGuiSystem : System(EntityQuery.none()) {

    override fun update(delta: Seconds, entity: Entity) = Unit

    abstract fun gui(builder: WidgetBuilder)

    override fun onGameStarted(engine: Engine) {
        emit(RegisterImGuiSystem(this))
    }
}
