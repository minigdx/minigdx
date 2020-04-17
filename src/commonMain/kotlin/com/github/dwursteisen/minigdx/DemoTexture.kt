package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.entity.models.Camera
import com.github.dwursteisen.minigdx.entity.primitives.Texture
import com.github.dwursteisen.minigdx.graphics.clear
import com.github.dwursteisen.minigdx.shaders.DefaultShaders

@ExperimentalStdlibApi
class DemoTexture : Game {

    override val worldSize: WorldSize = WorldSize(200, 200)

    private val camera = Camera.create(90f, worldSize.ratio, 1, 10)

    // 2d, you get it?
    private val dd = DefaultShaders.create2d()

    @ExperimentalStdlibApi
    private val texture: Texture by fileHandler.get("f-texture.png")

    override fun render(delta: Seconds) {
        clear(0f, 0f, 0f)
        dd.render {
            // camera.draw(it)
            texture.draw(it)
        }
    }
}
