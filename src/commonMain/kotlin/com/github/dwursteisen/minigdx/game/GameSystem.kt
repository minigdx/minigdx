package com.github.dwursteisen.minigdx.game

import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.WorldResolution
import com.github.dwursteisen.minigdx.ecs.Engine

abstract class GameSystem(var screen: Screen) : Game {

    override val worldResolution: WorldResolution = WorldResolution(400, 400)

    private val engine = Engine()

    override fun create() {
        screen.createSystems()?.forEach { engine.addSystem(it) }
        val renderStage = screen.createRenderStage()
        renderStage.forEach { engine.addSystem(it) }
        screen.createEntities(engine)
        renderStage.forEach { it.compile() }
    }

    override fun render(delta: Seconds) {
        screen.render(engine, delta)
    }

    override fun destroy() {
        screen.destroy(engine)
    }
}
