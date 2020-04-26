package com.github.dwursteisen.minigdx.demo

import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.WorldSize
import com.github.dwursteisen.minigdx.entity.delegate.Model
import com.github.dwursteisen.minigdx.entity.models.Camera3D
import com.github.dwursteisen.minigdx.fileHandler
import com.github.dwursteisen.minigdx.graphics.clear
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.inputs
import com.github.dwursteisen.minigdx.shaders.DefaultShaders

@ExperimentalStdlibApi
class DemoKey : Game {

    override val worldSize = WorldSize(200, 200)

    private val camera = Camera3D.perspective(45, worldSize.ratio, 1, 100)

    private val program = DefaultShaders.create3d()

    private val up: Model by fileHandler.copy("key.protobuf")
    private val down: Model by fileHandler.copy("key.protobuf")
    private val left: Model by fileHandler.copy("key.protobuf")
    private val right: Model by fileHandler.copy("key.protobuf")

    @ExperimentalStdlibApi
    override fun create() {
        camera.translate(0, 0f, -15f)

        up.rotateX(90f)
            .translate(0f, 0f, -1f)
            .rotateY(-90f)

        left.rotateX(90f)
            .translate(-1f, 0f, 0f)

        right.rotateX(90f)
            .translate(1f, 0f, 0f)
            .setRotationY(180f)

        down.rotateX(90f)
            .translate(0f, 0f, 1f)
            .rotateY(90f)
    }

    override fun render(delta: Seconds) {
        // --- act ---
        if (inputs.isKeyPressed(Key.ARROW_LEFT)) {
                left.translate(0, 5f * delta, 0f)
        } else if (inputs.isKeyPressed(Key.ARROW_RIGHT)) {
                right.translate(0, 5f * delta, 0f)
        } else if (inputs.isKeyJustPressed(Key.ARROW_UP)) {
                up.translate(0, 5f * delta, 0f)
        } else if (inputs.isKeyJustPressed(Key.ARROW_DOWN)) {
                down.translate(0, 5f * delta, 0f)
        }
        // --- draw ---
        clear(1 / 255f, 191 / 255f, 255 / 255f)

        program.render {
            camera.draw(it)
            left.draw(it)
            right.draw(it)
            up.draw(it)
            down.draw(it)
        }
    }
}
