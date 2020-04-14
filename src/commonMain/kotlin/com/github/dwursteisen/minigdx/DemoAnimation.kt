package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.entity.animations.AnimatedModel
import com.github.dwursteisen.minigdx.entity.models.Camera
import com.github.dwursteisen.minigdx.graphics.clear
import com.github.dwursteisen.minigdx.shaders.DefaultShaders

@ExperimentalStdlibApi
class DemoAnimation : Game {

    override val worldSize = WorldSize(200, 200)

    private val camera = Camera.create(45, worldSize.ratio, 1, 100)

    private val program = DefaultShaders.create3d()

    private val animatedModel: AnimatedModel by fileHandler.get("F.protobuf")

    @ExperimentalStdlibApi
    override fun create() {
        camera.translate(0, -5, -20)
    }

    override fun render(delta: Seconds) {
        animatedModel.update(delta)

        // --- draw ---
        clear(1 / 255f, 191 / 255f, 255 / 255f)

        program.render {
            camera.draw(it)
            animatedModel.draw(it)
        }
    }
}
