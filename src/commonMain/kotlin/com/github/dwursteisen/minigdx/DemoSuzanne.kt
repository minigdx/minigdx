package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.entity.delegate.Model
import com.github.dwursteisen.minigdx.entity.models.Camera3D
import com.github.dwursteisen.minigdx.graphics.clear
import com.github.dwursteisen.minigdx.shaders.DefaultShaders

@ExperimentalStdlibApi
class DemoSuzanne : Game {

    override val worldSize = WorldSize(200, 200)

    private val camera = Camera3D.perspective(45, worldSize.ratio, 1, 100)

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
