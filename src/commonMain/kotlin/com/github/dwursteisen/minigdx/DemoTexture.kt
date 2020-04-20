package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.entity.primitives.Texture
import com.github.dwursteisen.minigdx.entity.text.Text
import com.github.dwursteisen.minigdx.graphics.clear
import com.github.dwursteisen.minigdx.shaders.DefaultShaders
import kotlin.math.cos
import kotlin.math.sin

@ExperimentalStdlibApi
class DemoTexture : Game {

    override val worldSize: WorldSize = WorldSize(200, 200)

    // 2d, you get it?
    private val dd = DefaultShaders.create2d()

    @ExperimentalStdlibApi
    private val texture: Texture by fileHandler.get("f-texture.png")

    private var time = 0f

    private val text: Text by fileHandler.get("font")

    override fun create() {
        text.text = """
            ABCDEFGHIJKLMONPQRSTUVWXYZ
            abcdefghijklmonpqrstuvwxyz
        """.trimIndent()
    }

    override fun render(delta: Seconds) {
        time += delta
        clear(0f, 0f, 0f)

        dd.render {
            texture.setTranslate(100f + 20f * cos(time), 100f, 100f)
            texture.setScale(x = cos(time), y = sin(time))
            texture.draw(it)

            text.setTranslate(100f + 40f * -cos(time), 100f + 40f * sin(time), 100f)
            text.draw(it)
        }
    }
}
