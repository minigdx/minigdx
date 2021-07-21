package com.github.dwursteisen.minigdx.graphics

import com.dwursteisen.minigdx.scene.api.common.Id
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.file.Texture
import com.github.dwursteisen.minigdx.render.Stage

class FrameBuffer(
    val name: String,
    gameContext: GameContext,
    val stages: List<Stage>,
    val dependencies: List<FrameBuffer> = emptyList()
) : System(gameContext = gameContext), Stage {

    val gl = gameContext.gl

    private val frameBuffer = gl.createFrameBuffer()

    private var shaderCompiled: Boolean = false

    // FIXME: la faille de la texture ne devrait pas être fixe.
    val texture: Texture = Texture(Id(), ByteArray(16 * 16 * 4) { 1 }, 16, 16, false).also {
        gameContext.assetsManager.add(it)
    }

    /**
     * Compile shaders of dependencies and used stages.
     */
    override fun compileShaders() {
        // Don't compile shader if already compiled as a dependency for example.
        if (shaderCompiled) return
        dependencies.forEach { child ->
            child.compileShaders()
        }

        stages.forEach { stage ->
            stage.compileShaders()
        }
        shaderCompiled = true
    }

    /**
     * Render all dependencies of this frameBuffer so it can be directly use.
     */
    fun prepareDependencies(delta: Seconds) {
        dependencies.forEach { child ->
            child.prepareDependencies(delta)
            child.render(delta)
        }
    }

    /**
     * Render into the FrameBuffer by using stages.
     *
     * Only a FrameBuffer should call this method as the viewport might be miss configured after usage.
     */
    fun render(delta: Seconds) {
        gl.bindFrameBuffer(frameBuffer)
        gl.frameBufferTexture2D(GL.COLOR_ATTACHMENT0, texture.textureReference!!, 0)

        gameContext.viewport.update(gl, texture.width, texture.height, texture.width, texture.height)

        stages.forEach { stage ->
            stage.update(delta)
        }
    }

    /**
     * Update dependencies and render the frameBuffer.
     * The viewport is set back to the game screen at the end.
     */
    override fun update(delta: Seconds) {
        prepareDependencies(delta)
        render(delta)
        // render to the canvas
        gl.bindDefaultFrameBuffer()
        // Set the viewport back
        gameContext.viewport.update(
            gl,
            gameContext.frameBufferScreen.width,
            gameContext.frameBufferScreen.height,
            gameContext.gameScreen.width,
            gameContext.gameScreen.height
        )
    }

    override fun update(delta: Seconds, entity: Entity) = Unit
}
