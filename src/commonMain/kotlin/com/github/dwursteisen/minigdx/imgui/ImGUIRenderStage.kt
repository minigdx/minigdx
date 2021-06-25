package com.github.dwursteisen.minigdx.imgui

import com.github.dwursteisen.minigdx.GL
import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.events.Event
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.file.Texture
import com.github.dwursteisen.minigdx.graphics.GLResourceClient
import com.github.dwursteisen.minigdx.render.RenderStage
import com.github.dwursteisen.minigdx.shaders.fragment.UVFragmentShader
import com.github.dwursteisen.minigdx.shaders.vertex.MeshVertexShader
import com.github.minigdx.imgui.InputCapture
import com.github.minigdx.imgui.gui
import com.github.minigdx.imgui.internal.Resolution

class ImGUIRenderStage(
    private val gameContext: GameContext
) : RenderStage<MeshVertexShader, UVFragmentShader>(
    gameContext.gl,
    gameContext.glResourceClient,
    MeshVertexShader(),
    UVFragmentShader(),
    EntityQuery.none(),
    EntityQuery.none()
) {

    private val guiRenderer = ImGUI(gameContext, { program }, vertex, fragment)

    private val inputCapture: InputCapture = ImgCapture { input }

    private var systems: List<ImGuiSystem> = emptyList()

    override fun onEvent(event: Event, entityQuery: EntityQuery?) {
        if (event is RegisterImGuiSystem) {
            systems = systems + event.system
        }
    }

    override fun update(delta: Seconds) {
        gl.useProgram(program)
        systems.forEach {
            gui(
                renderer = guiRenderer,
                inputCapture = inputCapture,
                gameResolution = Resolution(gameContext.gameScreen.width, gameContext.gameScreen.height),
                builder = { it.gui(this) }
            )
        }
    }

    override fun update(delta: Seconds, entity: Entity) = Unit
}
