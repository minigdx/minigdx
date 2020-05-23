package com.github.dwursteisen.minigdx.demo

import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.WorldResolution
import com.github.dwursteisen.minigdx.entity.delegate.Drawable
import com.github.dwursteisen.minigdx.entity.models.Camera2D
import com.github.dwursteisen.minigdx.entity.models.Camera3D
import com.github.dwursteisen.minigdx.entity.models.Light
import com.github.dwursteisen.minigdx.entity.text.Text
import com.github.dwursteisen.minigdx.fileHandler
import com.github.dwursteisen.minigdx.graphics.clear
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.inputs
import com.github.dwursteisen.minigdx.shaders.DefaultShaders

@ExperimentalStdlibApi
class DemoLight : Game {

    override val worldResolution: WorldResolution = WorldResolution(1024, 1024)

    private val camera = Camera3D.perspective(45, worldResolution.ratio, 1, 100)

    private val gui = Camera2D.orthographic(worldResolution)

    private val program = DefaultShaders.create3d()

    private val program2d = DefaultShaders.create2d()

    private val model: Drawable by fileHandler.get("suzanne.protobuf")

    private val text: Text by fileHandler.get("font")

    private val light: Light = Light()

    @ExperimentalStdlibApi
    override fun create() {
        camera.translate(0, 0, -5)
        text.text = "Hit left/right arrow to change the direction of the light."
        text.setTranslate(5f, 5f)
    }

    override fun render(delta: Seconds) {
        // --- act ---
        // light.setRotationX(sin(time))
        if (inputs.isKeyPressed(Key.ARROW_LEFT)) {
            light.translate(x = -5f * delta)
        } else if (inputs.isKeyPressed(Key.ARROW_RIGHT)) {
            light.translate(x = 5f * delta)
        }
        // --- draw ---
        clear(1 / 255f, 191 / 255f, 255 / 255f)

        program.render {
            camera.draw(it)
            light.draw(it)
            model.draw(it)
        }

        program2d.render {
            gui.draw(it)
            text.draw(it)
        }
    }
}
