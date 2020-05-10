package com.github.dwursteisen.minigdx.demo

import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.WorldResolution
import com.github.dwursteisen.minigdx.entity.models.Camera2D
import com.github.dwursteisen.minigdx.entity.primitives.Texture
import com.github.dwursteisen.minigdx.entity.text.Text
import com.github.dwursteisen.minigdx.fileHandler
import com.github.dwursteisen.minigdx.graphics.clear
import com.github.dwursteisen.minigdx.input.Key
import com.github.dwursteisen.minigdx.inputs
import com.github.dwursteisen.minigdx.shaders.DefaultShaders
import kotlin.math.cos
import kotlin.math.sin

@ExperimentalStdlibApi
class DemoTexture : Game {

    override val worldResolution: WorldResolution =
        WorldResolution(512, 512)

    private val camera = Camera2D.orthographic(worldResolution)

    // 2d, you get it?
    private val dd = DefaultShaders.create2d()

    @ExperimentalStdlibApi
    private val texture: Texture by fileHandler.copy("f-texture.png")

    private var time = 0f

    private val text: Text by fileHandler.copy("font")

    override fun create() {
        text.text = """
            ABCDEFGHIJKLMONPQRSTUVWXYZ
            abcdefghijklmonpqrstuvwxyz
        """.trimIndent()
    }

    override fun render(delta: Seconds) {
        time += delta
        clear(0f, 0f, 0f)

        if (inputs.isKeyPressed(Key.ARROW_LEFT)) {
            camera.translate(x = -15f * delta, y = 0f, z = 0f)
        } else if (inputs.isKeyPressed(Key.ARROW_RIGHT)) {
            camera.translate(x = 15f * delta, y = 0f, z = 0f)
        }

        if (inputs.isKeyPressed(Key.ARROW_UP)) {
            camera.translate(y = -15f * delta, x = 0f, z = 0f)
        } else if (inputs.isKeyPressed(Key.ARROW_DOWN)) {
            camera.translate(y = 15f * delta, x = 0f, z = 0f)
        }

        dd.render {
            camera.draw(it)

            texture.setTranslate(256f, 256f, 100f)
            texture.setScale(x = cos(time), y = sin(time))
            texture.draw(it)

            text.setTranslate(100f + 40f * -cos(time), 100f + 40f * sin(time), 100f)
            text.draw(it)
        }
    }
}
