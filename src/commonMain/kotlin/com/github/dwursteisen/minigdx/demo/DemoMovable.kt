package com.github.dwursteisen.minigdx.demo

import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.WorldResolution
import com.github.dwursteisen.minigdx.entity.models.Camera3D
import com.github.dwursteisen.minigdx.entity.models.Cube
import com.github.dwursteisen.minigdx.entity.models.Light
import com.github.dwursteisen.minigdx.entity.primitives.Colors
import com.github.dwursteisen.minigdx.shaders.DefaultShaders

class DemoMovable : Game {

    override val worldResolution: WorldResolution = WorldResolution(200, 200)

    private val cubes = listOf(
        Cube("0", Colors.WHITE),
        Cube("1", Colors.RED).also {
            it.translate(y = 2f)
        },
        Cube("2", Colors.GREEN).also {
            it.translate(x = 2f, y = 2f)
        },
        Cube("3", Colors.BLUE).also {
            it.translate(y = 4f)
        },
        Cube("4", Colors.BLACK).also {
            it.translate(y = 6f)
        },
        Cube("5", Colors.WHITE).also {
            it.translate(x = 2f, y = 6f)
        }
    )

    private val camera = Camera3D.perspective(90f, worldResolution.ratio, 0.1f, 100f)

    private val shaderProgram = DefaultShaders.create3d()

    private val light: Light = Light()

    override fun create() {
        camera.translate(z = -10f)
    }

    override fun render(delta: Seconds) {
        shaderProgram.render { shader ->
            camera.draw(shader)
            camera.control(delta)
            light.draw(shader)
            cubes.forEach {
                it.draw(shader)
            }
        }
    }
}
