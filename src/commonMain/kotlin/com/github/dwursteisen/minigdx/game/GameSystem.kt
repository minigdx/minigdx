package com.github.dwursteisen.minigdx.game

import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine

abstract class GameSystem(val gameContext: GameContext, var screen: Screen) : Game {

    private val engine = Engine()

    override fun create() {
        screen.createSystems(engine).forEach { engine.addSystem(it) }
        val renderStage = screen.createRenderStage(gameContext.gl)
        renderStage.forEach { engine.addSystem(it) }
        screen.createEntities(engine)
        renderStage.forEach { it.compileShaders() }
    }

    override fun render(delta: Seconds) {
        screen.render(engine, delta)
    }

    override fun destroy() {
        screen.destroy(engine)
    }
}
