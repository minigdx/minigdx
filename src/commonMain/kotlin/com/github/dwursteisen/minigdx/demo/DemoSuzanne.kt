package com.github.dwursteisen.minigdx.demo

import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.WorldResolution
import com.github.dwursteisen.minigdx.entity.delegate.Model
import com.github.dwursteisen.minigdx.entity.models.Camera3D
import com.github.dwursteisen.minigdx.fileHandler
import com.github.dwursteisen.minigdx.graphics.clear
import com.github.dwursteisen.minigdx.shaders.DefaultShaders

@ExperimentalStdlibApi
class DemoSuzanne : Game {

    override val worldResolution = WorldResolution(200, 200)

    private val camera = Camera3D.perspective(45, worldResolution.ratio, 1, 100)

    private val program = DefaultShaders.create3d()

    private val model: Model by fileHandler.get("suzanne.protobuf")

    @ExperimentalStdlibApi
    override fun create() {
        camera.translate(0, 0, -5)
    }

    override fun render(delta: Seconds) {
        // --- act ---
        model.rotateY(delta * 10)

        // --- draw ---
        clear(1 / 255f, 191 / 255f, 255 / 255f)

        program.render {
            camera.draw(it)
            model.draw(it)
        }
    }
}
