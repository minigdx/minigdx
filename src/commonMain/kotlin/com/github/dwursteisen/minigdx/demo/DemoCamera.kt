package com.github.dwursteisen.minigdx.demo

import com.curiouscreature.kotlin.math.degrees
import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.WorldResolution
import com.github.dwursteisen.minigdx.entity.models.Camera3D
import com.github.dwursteisen.minigdx.entity.models.Cube
import com.github.dwursteisen.minigdx.entity.models.Landmark
import com.github.dwursteisen.minigdx.entity.models.Scene
import com.github.dwursteisen.minigdx.entity.primitives.Colors
import com.github.dwursteisen.minigdx.fileHandler
import com.github.dwursteisen.minigdx.graphics.clear
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.inputs
import com.github.dwursteisen.minigdx.shaders.DefaultShaders

@ExperimentalStdlibApi
class DemoCamera : Game {

    override val worldResolution: WorldResolution = WorldResolution(200, 200)

    private val scene by fileHandler.get<Scene>("camera.protobuf")

    private val shader = DefaultShaders.create3d()

    private val myCamera = Camera3D.perspective(
        degrees(0.3995964825153351f), worldResolution.ratio, 0.001f, 100f
    )

    private val myCube = Cube(name = "test")

    private val landmark = Landmark.of()

    override fun create() {
        myCamera.translate(z = -10f)
    }

    override fun render(delta: Seconds) {
        clear(Colors.BLUE)

        if (inputs.isKeyPressed(Key.SPACE)) {
            landmark.rotateX(30f * delta)
        }

        myCamera.control(delta)

        shader.render { program ->
            myCamera.draw(program)
            /*scene.camera["Camera"]?.run {
                println("camera ${this.position}")
                draw(program)
            }
            */
            scene.models.values.forEach {
                // println("draw model")
                //  it.draw(program)
            }

            landmark.draw(program)
            // myCube.draw(program)
        }
    }
}
