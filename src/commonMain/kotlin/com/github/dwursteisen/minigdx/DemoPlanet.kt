package com.github.dwursteisen.minigdx

import com.github.dwursteisen.minigdx.entity.delegate.Model
import com.github.dwursteisen.minigdx.entity.models.Camera3D
import com.github.dwursteisen.minigdx.graphics.clear
import com.github.dwursteisen.minigdx.input.TouchSignal
import com.github.dwursteisen.minigdx.shaders.DefaultShaders

@ExperimentalStdlibApi
class DemoPlanet : Game {

    override val worldSize = WorldSize(200, 200)

    private val camera = Camera3D.perspective(45, worldSize.ratio, 1, 100)

    private val program = DefaultShaders.create3d()

    private val model: Model by fileHandler.get("planet.protobuf")

    @ExperimentalStdlibApi
    override fun create() {
        camera.translate(0, 0, -5)
    }

    private var rotationStart: Float? = null
    private var currentRotation: Float = 0f
    private var xStart = 0f

    override fun render(delta: Seconds) {
        // --- act ---
        val cursor = inputs.isTouched(TouchSignal.TOUCH1)
        if (cursor != null) {
            if (rotationStart == null) {
                rotationStart = model.rotation.y
                xStart = cursor.x
            }

            val screenWidth = gl.screen.width
            val factor = (cursor.x - screenWidth * 0.5f) / screenWidth
            currentRotation = factor * 180f
            rotationStart?.run {
                model.setRotationY(this + currentRotation)
            }
        } else {
            rotationStart = null
            model.rotateY(delta * 10)
        }

        // --- draw ---
        clear(1 / 255f, 191 / 255f, 255 / 255f)

        program.render {
            camera.draw(it)
            model.draw(it)
        }
    }
}
