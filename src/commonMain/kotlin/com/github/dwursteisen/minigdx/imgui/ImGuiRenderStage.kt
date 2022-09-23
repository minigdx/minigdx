package com.github.dwursteisen.minigdx.imgui

import com.github.dwursteisen.minigdx.GameContext
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.ecs.Engine
import com.github.dwursteisen.minigdx.ecs.entities.Entity
import com.github.dwursteisen.minigdx.ecs.systems.EntityQuery
import com.github.dwursteisen.minigdx.file.Texture
import com.github.dwursteisen.minigdx.file.get
import com.github.dwursteisen.minigdx.render.RenderStage
import com.github.dwursteisen.minigdx.shaders.fragment.UVFragmentShader
import com.github.dwursteisen.minigdx.shaders.vertex.MeshVertexShader
import com.github.minigdx.imgui.ImGui

class ImGuiRenderStage(
    gameContext: GameContext
) : RenderStage<MeshVertexShader, UVFragmentShader>(
    gameContext,
    MeshVertexShader(),
    UVFragmentShader(),
    EntityQuery.none(),
    EntityQuery.none()
) {

    private val guiRenderer = ImGuiBatchRender(gameContext, vertex, fragment)

    private val texture: Texture by gameContext.fileHandler.get("internal/widgets.png")

    override fun onGameStarted(engine: Engine) {
        ImGui.setup(texture, ImGuiInputCapture(input))
    }

    override fun update(delta: Seconds) {
        guiRenderer.program = program
        gl.useProgram(program)

        ImGui.beginFrame()
        ImGui.batch.forEach {
            guiRenderer.render(
                it.texture as Texture,
                it.vertices.toFloatArray(),
                it.uvs.toFloatArray(),
                it.verticesOrder.toIntArray()
            )
        }
        ImGui.endFrame()
    }

    override fun update(delta: Seconds, entity: Entity) = Unit
}
