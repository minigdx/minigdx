package com.github.dwursteisen.minigdx.demo

import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.WorldResolution
import com.github.dwursteisen.minigdx.entity.delegate.Drawable
import com.github.dwursteisen.minigdx.entity.models.Camera3D
import com.github.dwursteisen.minigdx.entity.models.Light
import com.github.dwursteisen.minigdx.fileHandler
import com.github.dwursteisen.minigdx.graphics.clear
import com.github.dwursteisen.minigdx.shaders.DefaultShaders

@ExperimentalStdlibApi
class DemoSuzanne2 : Game {

    override val worldResolution = WorldResolution(200, 200)

    private val camera = Camera3D.perspective(45, worldResolution.ratio, 1, 100)

    private val program = DefaultShaders.create3d()

    private val model: Drawable by fileHandler.get("monkey_color.protobuf")

    private val light: Light = Light()

    @ExperimentalStdlibApi
    override fun create() {
        camera.translate(0, 0, -5)
        model.rotateX(-90f)
    }

    override fun render(delta: Seconds) {
        // --- act ---
        model.rotateZ(-delta * 10)
        // --- draw ---
        clear(1 / 255f, 191 / 255f, 255 / 255f)

        program.render {
            camera.draw(it)
            light.draw(it)
            model.draw(it)
        }
    }
}
