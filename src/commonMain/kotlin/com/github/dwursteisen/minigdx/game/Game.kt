package com.github.dwursteisen.minigdx.game

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.components.UICamera
import com.github.dwursteisen.minigdx.ecs.components.UIComponent
import com.github.dwursteisen.minigdx.ecs.components.gl.MeshPrimitive
import com.github.dwursteisen.minigdx.ecs.entities.EntityFactory
import com.github.dwursteisen.minigdx.ecs.systems.ArmatureUpdateSystem
import com.github.dwursteisen.minigdx.ecs.systems.CameraTrackSystem
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.ecs.systems.ScriptExecutorSystem
import com.github.dwursteisen.minigdx.ecs.systems.SpriteAnimatedSystem
import com.github.dwursteisen.minigdx.ecs.systems.System
import com.github.dwursteisen.minigdx.ecs.systems.TextEffectSystem
import com.github.dwursteisen.minigdx.graphics.GLResourceClient
import com.github.dwursteisen.minigdx.render.AnimatedMeshPrimitiveRenderStage
import com.github.dwursteisen.minigdx.render.BoundingBoxStage
import com.github.dwursteisen.minigdx.render.ClearBufferRenderStage
import com.github.dwursteisen.minigdx.render.MeshPrimitiveRenderStage
import com.github.dwursteisen.minigdx.render.RenderStage

interface Game {

    val gameContext: GameContext

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
     * Create render stages.
     *
     * Can be override but need extra care when doing it.
     */
    fun createRenderStage(gl: GL, compiler: GLResourceClient): List<RenderStage<*, *>> {
        val stages = mutableListOf<RenderStage<*, *>>()
        stages.add(ClearBufferRenderStage(gl, compiler))
        stages.add(MeshPrimitiveRenderStage(gl, compiler))
        stages.add(AnimatedMeshPrimitiveRenderStage(gl, compiler))
        if (gameContext.options.debug) {
            stages.add(BoundingBoxStage(gl, compiler))
        }

        // display UI component only trough the UI Camera
        stages.add(
            MeshPrimitiveRenderStage(
                gl,
                compiler,
                query = EntityQuery(
                    listOf(MeshPrimitive::class, UIComponent::class)
                ),
                cameraQuery = EntityQuery(UICamera::class)
            )
        )
        return stages
    }

    fun render(engine: Engine, delta: Seconds) {
        engine.update(delta)
    }

    fun destroy(engine: Engine) = engine.destroy()
}
