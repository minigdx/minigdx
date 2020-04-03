package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.entity.Camera
import com.github.dwursteisen.minigdx.entity.animations.AnimatedModel
import com.github.dwursteisen.minigdx.graphics.clear
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.shaders.DefaultShaders

@ExperimentalStdlibApi
class DemoAnimation2 : Game {

    override val worldSize = WorldSize(200, 200)

    private val camera = Camera.create(45, worldSize.ratio, 1, 100)

    private val program = DefaultShaders.create()

    private val animatedModel: AnimatedModel by fileHandler.get("monkey_animation.protobuf")

    @ExperimentalStdlibApi
    override fun create() {
        camera.translate(0, 0, -5)
        animatedModel.rotateY(90f)
    }

    override fun render(delta: Seconds) {

        if (inputs.isKeyPressed(Key.F)) {
            animatedModel.rotateY(10f * delta)
        } else if (inputs.isKeyPressed(Key.H)) {
            animatedModel.rotateY(-10f * delta)
        }

        if (inputs.isKeyJustPressed(Key.G)) {
            animatedModel.switchAnimation("TODO")
        }

        animatedModel.update(delta * 0.5f)
        // --- draw ---
        clear(1 / 255f, 191 / 255f, 255 / 255f)

        program.render {
            camera.draw(it)
            animatedModel.draw(it)
        }
    }
}
