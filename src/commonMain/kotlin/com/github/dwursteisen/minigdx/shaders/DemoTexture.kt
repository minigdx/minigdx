package com.github.dwursteisen.minigdx.shaders

import com.github.dwursteisen.minigdx.Game
import com.github.dwursteisen.minigdx.Seconds
import com.github.dwursteisen.minigdx.WorldSize
import com.github.dwursteisen.minigdx.entity.models.Camera

class DemoTexture : Game {

    override val worldSize: WorldSize = WorldSize(200, 200)

    private val camera = Camera.create(90f, worldSize.ratio, 1, 10)

    // 2d, you get it?
    private val dd = DefaultShaders.create2d()

    override fun render(delta: Seconds) {
        dd.render {
        }
    }
}
