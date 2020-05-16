package com.github.dwursteisen.minigdx.demo

import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.WorldResolution
import com.github.dwursteisen.minigdx.entity.models.Scene
import com.github.dwursteisen.minigdx.entity.primitives.Colors
import com.github.dwursteisen.minigdx.fileHandler
import com.github.dwursteisen.minigdx.graphics.clear
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.inputs
import com.github.dwursteisen.minigdx.log
import com.github.dwursteisen.minigdx.shaders.DefaultShaders

@ExperimentalStdlibApi
class DemoCamera : Game {

    override val worldResolution: WorldResolution = WorldResolution(200, 200)

    private val scene by fileHandler.get<Scene>("camera.protobuf")
    private val cameras by fileHandler.get<Scene>("camera.protobuf").map { it.camera.values.toList() }
    private val shader = DefaultShaders.create3d()

    private var cameraIndex = 0

    override fun render(delta: Seconds) {
        clear(Colors.BLUE)

        if (inputs.isKeyJustPressed(Key.SPACE)) {
            cameraIndex = (cameraIndex + 1) % cameras.size
            log.info("DEMO_CAMERA") { "Switch to camera ${cameraIndex + 1}/ ${cameras.size}" }
        }

        shader.render { program ->
            cameras[cameraIndex].draw(program)
            cameras[cameraIndex].control(delta)

            scene.models.values.forEach {
                it.draw(program)
            }
        }
    }
}
