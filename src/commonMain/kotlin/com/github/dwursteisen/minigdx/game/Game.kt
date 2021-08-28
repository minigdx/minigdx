package com.github.dwursteisen.minigdx.game

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Options
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.Color
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactory
import com.github.dwursteisen.minigdx.ecs.systems.ArmatureUpdateSystem
import com.github.dwursteisen.minigdx.ecs.systems.CameraTrackSystem
import com.github.dwursteisen.minigdx.ecs.systems.ScriptExecutorSystem
import com.github.dwursteisen.minigdx.ecs.systems.SpriteAnimatedSystem
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.ecs.systems.TextEffectSystem
import com.github.dwursteisen.minigdx.file.AssetsManagerSystem
import com.github.dwursteisen.minigdx.graphics.FrameBuffer
import com.github.dwursteisen.minigdx.imgui.ImGUIRenderStage
import com.github.dwursteisen.minigdx.render.AnimatedModelRenderStage
import com.github.dwursteisen.minigdx.render.BoundingBoxRenderStage
import com.github.dwursteisen.minigdx.render.ClearBufferRenderStage
import com.github.dwursteisen.minigdx.render.ModelComponentRenderStage
import com.github.dwursteisen.minigdx.render.RenderStage

interface Game {

    val gameContext: GameContext

    /**
     * Configure the clear color of the game (ie: which color the default background will be)
     *
     * [null] value means that the background will NOT be cleared.
     */
    val clearColor: Color?
        get() = Color(1f, 1f, 1f, 1f)

    /**
     * Create entities used to bootstrap the game
     * (ie: all entities required for the first level of a game)
     */
    fun createEntities(entityFactory: EntityFactory)

    /**
     * Create default system. There are mostly technicals.
     * It can be override (to get ride of a useless system for example)
     * but in such case, the engine will run without the feature associated to
     * the system.
     */
    fun createDefaultSystems(engine: Engine): List<System> = listOf(
        SpriteAnimatedSystem(),
        ArmatureUpdateSystem(),
        ScriptExecutorSystem(),
        TextEffectSystem(),
        CameraTrackSystem()
    )

    /**
     * Create the game systems. Most systems will be "game" systems component.
     * Technical systems can be added, like a custom camera tracking system.
     */
    fun createSystems(engine: Engine): List<System>

    /**
     * Create system that are executed before the rendering systems.
     * Will be use to prepare data before the rendering phase
     */
    fun createPostRenderSystem(engine: Engine): List<System> {
        return listOf(AssetsManagerSystem(gameContext.assetsManager))
    }

    /**
     * Create frame buffers, to create a shader pipeline.
     */
    fun createFrameBuffers(gameContext: GameContext): List<FrameBuffer> {
        return emptyList()
    }

    /**
     * Create render stages.
     *
     * Can be override but need extra care when doing it.
     *
     */
    fun createRenderStage(): List<RenderStage<*, *>> {
        val stages = mutableListOf<RenderStage<*, *>>()
        clearColor?.run {
            stages.add(ClearBufferRenderStage(gameContext, this))
        }
        stages.add(ModelComponentRenderStage(gameContext))
        stages.add(AnimatedModelRenderStage(gameContext))
        return stages
    }

    fun createDebugRenderStage(options: Options): List<RenderStage<*, *>> {
        val stages = mutableListOf<RenderStage<*, *>>()
        if (options.debug) {
            stages.add(BoundingBoxRenderStage(gameContext))
        }
        stages.add(ImGUIRenderStage(gameContext))
        return stages
    }

    fun render(engine: Engine, delta: Seconds) {
        engine.update(delta)
    }

    fun destroy(engine: Engine) = engine.destroy()
}
