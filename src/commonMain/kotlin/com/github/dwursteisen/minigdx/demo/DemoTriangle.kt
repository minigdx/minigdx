package com.github.dwursteisen.minigdx.demo

import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.WorldResolution
import com.github.dwursteisen.minigdx.entity.delegate.Drawable
import com.github.dwursteisen.minigdx.entity.models.Camera3D
import com.github.dwursteisen.minigdx.entity.models.Landmark
import com.github.dwursteisen.minigdx.entity.models.Light
import com.github.dwursteisen.minigdx.entity.primitives.Colors
import com.github.dwursteisen.minigdx.entity.primitives.Mesh
import com.github.dwursteisen.minigdx.entity.primitives.Vertice
import com.github.dwursteisen.minigdx.graphics.clear
import com.github.dwursteisen.minigdx.math.Vector3
import com.github.dwursteisen.minigdx.shaders.DefaultShaders

@ExperimentalStdlibApi
class DemoTriangle : Game {

    override val worldResolution = WorldResolution(200, 200)

    private val camera = Camera3D.perspective(45, worldResolution.ratio, 1, 100)

    private val program = DefaultShaders.create3d()

    private val model: Drawable = Drawable(
        mesh = Mesh(
            "model",
            vertices = arrayOf(
                Vertice(
                    position = Vector3(-0.5f, 0f, 0f),
                    color = Colors.RED
                ),
                Vertice(
                    position = Vector3(0.5f, 0f, 0f),
                    color = Colors.BLUE
                ),
                Vertice(
                    position = Vector3(0f, 0.5f, 0f),
                    color = Colors.GREEN
                )
            ),
            verticesOrder = shortArrayOf(
                0, 1, 2
            )
        )
    )

    private val light: Light = Light()

    @ExperimentalStdlibApi
    override fun create() {
        camera.translate(0, 0, -5)
    }

    private val landmark = Landmark.of()

    override fun render(delta: Seconds) {
        // --- draw ---
        clear(1 / 255f, 191 / 255f, 255 / 255f)

        program.render {
            camera.draw(it)
            light.draw(it)
            model.draw(it)
            landmark.draw(it)
        }
    }
}
