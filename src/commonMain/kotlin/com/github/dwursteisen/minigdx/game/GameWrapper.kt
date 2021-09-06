package com.github.dwursteisen.minigdx.game

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.graphics.FrameBuffer
import com.github.dwursteisen.minigdx.render.ClearBufferRenderStage

class GameWrapper(val gameContext: GameContext, var game: Game) {

    private val engine = Engine(gameContext)

    fun create() {
        game.createDefaultSystems(engine).forEach { engine.addSystem(it) }
        game.createSystems(engine).forEach { engine.addSystem(it) }
        game.createPostRenderSystem(engine).forEach { engine.addSystem(it) }

        // FIXME: code cleanup
        fun traverse(frameBuffer: FrameBuffer): List<FrameBuffer> {
            return frameBuffer.dependencies.flatMap { traverse(it) } + frameBuffer
        }

        val frameBuffers = game.createFrameBuffers(gameContext)
        // Keep the frame buffers into the context
        gameContext.frameBuffers = frameBuffers
            .flatMap { traverse(it) }
            .associateBy { buffer -> buffer.name }

        // Check if there is one frame buffer that will render on screen
        // And therefore, will take the lead on the render stage.
        val rootFrameBuffers = frameBuffers.filter { frameBuffer -> frameBuffer.renderOnScreen }

        val renderStage = when (rootFrameBuffers.size) {
            0 -> game.createRenderStage()
            1 -> emptyList()
            else -> throw IllegalStateException(
                "Only one frame buffer can be render directly on screen." +
                    "Please configure Frame buffers so only one will render on screen by setting" +
                    " to false the property of one of those frame buffer: " +
                    "${rootFrameBuffers.joinToString { it.name }} "
            )
        }

        // When there is one frame buffer to be render on the screen, we add a clear stage before it
        // So the screen is clean.
        // It can be disable be removing the clear color of the game.
        if (rootFrameBuffers.size == 1) {
            game.clearColor?.run { engine.addSystem(ClearBufferRenderStage(gameContext, this)) }
        }

        frameBuffers.forEach { engine.addSystem(it) }

        val debugRenderStage = game.createDebugRenderStage(gameContext.options)

        (renderStage + debugRenderStage).forEach { engine.addSystem(it) }

        game.createEntities(engine.entityFactory)
        // Load assets that can be loaded
        gameContext.assetsManager.update()
        renderStage.forEach { it.compileShaders() }
        debugRenderStage.forEach { it.compileShaders() }
        frameBuffers.forEach { it.compileShaders() }
        engine.onGameStart()
    }

    fun resume() = Unit

    fun render(delta: Seconds) {
        game.render(engine, delta)
    }

    fun pause() = Unit

    fun destroy() {
        game.destroy(engine)
    }
}
