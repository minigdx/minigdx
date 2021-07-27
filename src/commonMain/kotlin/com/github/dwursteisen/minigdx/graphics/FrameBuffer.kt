package com.github.dwursteisen.minigdx.graphics

import com.dwursteisen.minigdx.scene.api.common.Id
import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Resolution
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.events.Event
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.file.Asset
import com.github.dwursteisen.minigdx.file.Texture
import com.github.dwursteisen.minigdx.render.Stage
import com.github.dwursteisen.minigdx.render.StageWithSystem

/**
 * A FrameBuffer is a buffer in which things
 * can be rendered in memory to be re used later on
 * for another rendering.
 */
open class FrameBuffer(
    /**
     * Name of the framebuffer.
     * The name can be use to find this
     * framebuffer later on.
     */
    val name: String,
    gameContext: GameContext,
    /**
     * Resolution of this frame buffer
     */
    val resolution: Resolution,
    /**
     * Stage used to render things on this framebuffer
     */
    val stages: List<StageWithSystem>,
    /**
     * Frame Buffer used by this frame buffer.
     */
    val dependencies: List<FrameBuffer> = emptyList(),
    /**
     * Is the frame buffer is rendered in memory or
     * should it be renderer on screen?
     */
    val renderOnScreen: Boolean = false,
    /**
     * Is a depth buffer attached to this frame buffer?
     */
    withDepthBuffer: Boolean = true
) : System(gameContext = gameContext), Stage, Asset {

    private val gl = gameContext.gl

    private val frameBuffer = gl.createFrameBuffer()

    private val depthBuffer = if (withDepthBuffer) {
        gl.createRenderBuffer()
    } else {
        null
    }

    private var shaderCompiled: Boolean = false

    private val _dependencies = dependencies.map { it.name to it }.toMap()

    val texture: Texture = createFrameBufferTexture(gameContext)

    init {
        gameContext.assetsManager.add(this)
    }

    private fun createFrameBufferTexture(gameContext: GameContext): Texture {
        // Create default white non transparent texture
        val textureData = ByteArray(resolution.width * resolution.height * 4) { 1 }
        return Texture(
            Id(),
            textureData,
            resolution.width,
            resolution.height,
            false
        ).also {
            gameContext.assetsManager.add(it)
        }
    }

    override fun load(gameContext: GameContext) {
        gl.bindFrameBuffer(frameBuffer)
        gl.frameBufferTexture2D(GL.COLOR_ATTACHMENT0, texture.textureReference!!, 0)

        depthBuffer?.run {
            gl.bindRenderBuffer(this)
            gl.renderBufferStorage(GL.DEPTH_COMPONENT16, texture.width, texture.height)
            gl.framebufferRenderbuffer(GL.DEPTH_ATTACHMENT, depthBuffer)
        }
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

    override fun onGameStarted(engine: Engine) {
        dependencies.forEach { child ->
            child.onGameStarted(engine)
        }

        stages.forEach { stage ->
            stage.onGameStarted(engine)
        }
    }

    override fun add(entity: Entity): Boolean {
        dependencies.forEach { child ->
            child.add(entity)
        }

        stages.forEach { stage ->
            stage.add(entity)
        }
        return super.add(entity)
    }

    override fun remove(entity: Entity): Boolean {
        dependencies.forEach { child ->
            child.remove(entity)
        }

        stages.forEach { stage ->
            stage.remove(entity)
        }
        return super.remove(entity)
    }

    override fun onEvent(event: Event, entityQuery: EntityQuery?) {
        dependencies.forEach { child ->
            child.onEvent(event, entityQuery)
        }

        stages.forEach { stage ->
            stage.onEvent(event, entityQuery)
        }
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

    fun getDependency(name: String): FrameBuffer {
        return _dependencies.getValue(name)
    }

    /**
     * Render into the FrameBuffer by using stages.
     *
     * Only a FrameBuffer should call this method as the viewport might be miss configured after usage.
     */
    private fun render(delta: Seconds) {
        gl.bindFrameBuffer(frameBuffer)

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
        if (renderOnScreen) {
            // This FrameBuffer will be render directly on the screen.
            // The default frame buffer will be bind before
            // rendering the actual frame buffer.
            // render to the canvas
            gl.bindDefaultFrameBuffer()
            gameContext.viewport.update(
                gl,
                gameContext.frameBufferScreen.width,
                gameContext.frameBufferScreen.height,
                gameContext.gameScreen.width,
                gameContext.gameScreen.height
            )
            stages.forEach { stage ->
                stage.update(delta)
            }
        } else {
            // The Frame Buffer will be render into a texture.
            // then the default frame buffer will be configured back.
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
    }

    override fun update(delta: Seconds, entity: Entity) = Unit
}
